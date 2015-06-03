package test1;

/**
 * Created by cathym on 2015/5/26.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;


class NewThread implements Runnable{

    static int numberOfSuccess = 0;

    String username = null;
    String password = null;
    String destIP = null;
    String sourceIP = null;
    ProtocolVersion httpVersion = HttpVersion.HTTP_1_0;  //HttpVersion.HTTP_1_1
    Thread t;

    NewThread(String usernameT, String passwordT, String destIPT, ProtocolVersion httpVersionT, String sourceIPT) {
        // 创建第二个新线程
        username = usernameT;
        password = passwordT;
        destIP = destIPT;
        httpVersion = httpVersionT;
        sourceIP = sourceIPT;

       ;
        t = new Thread(this, "Demo Thread");
        System.out.println("Child thread: " + t);
        t.run();
//        t.start(); // 开始线程
    }

    @Override
    public void run() {
        System.out.println("Begin" );
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(destIP); //https://iam-fed.juniper.net/auth/ilogin.html
        //����http version
        RequestConfig config = null;
        try {
            config = RequestConfig.custom().setLocalAddress(InetAddress.getByName(sourceIP)).build();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        httppost.setProtocolVersion(httpVersion);
        httppost.setConfig(config);

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity("hehe");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringEntity.setContentType("application/xml");
        stringEntity.setContentEncoding("UTF-8");
        httppost.setEntity(stringEntity);

        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httppost.addHeader("username", username);
        httppost.addHeader("password", password);
        try{
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println(response.getStatusLine());

                if(response.getStatusLine().getStatusCode() == 200){
                    numberOfSuccess ++;
                }
            }catch(Exception e ) {
                e.printStackTrace();
            }finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


public class HTTPDemo{

    public static void main(String[] args) throws Exception {
        String username = args[0];
        String password = args[1];
        String destIP = args[2];
        ProtocolVersion httpVersion = HttpVersion.HTTP_1_1;
        if(args[3].equals("0")){
            httpVersion = HttpVersion.HTTP_1_0;
        }

        int numberOfPost = args.length - 4;
        if(numberOfPost == 0){
            NewThread post = new NewThread(username,password,destIP,httpVersion,InetAddress.getLocalHost().getHostAddress().toString());
        }else{
            for(int i=0;i<numberOfPost;i++){
                new NewThread(username,password,destIP,httpVersion,args[i+4]);
            }
        }
//
        System.out.println("numberOfSuccessPost : "+NewThread.numberOfSuccess);

    }
}