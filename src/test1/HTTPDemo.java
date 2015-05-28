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
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;


public class HTTPDemo{
    public static void main(String[] args) throws Exception {

        String username = "username";
        String password = "password";
        ProtocolVersion httpVersion1_0 = HttpVersion.HTTP_1_0;
        ProtocolVersion httpVersion1_1 = HttpVersion.HTTP_1_1;


        System.out.println(args[0]);
        System.out.println(args[1]);
//        HttpHost proxy = new HttpHost("10.108.147.241", 80);
//        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//        CloseableHttpClient httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();

        for (String arg : args) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://192.168.0.10:80"); //https://iam-fed.juniper.net/auth/ilogin.html
            //����http version
            RequestConfig config = RequestConfig.custom().setLocalAddress(InetAddress.getByName(arg)).build();
            httppost.setProtocolVersion(httpVersion1_1);
            httppost.setConfig(config);

            StringEntity stringEntity = new StringEntity("hehe");
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
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        System.out.println("--------------------------------------");
                        System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                        System.out.println("--------------------------------------");
                    }
                    System.out.println(response.getStatusLine());

                } finally {
                    response.close();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // �ر�����,�ͷ���Դ
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // ����httppost,�������https��վҲ�ǿ��Ե�


    }
}