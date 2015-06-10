package test1;

/**
 * Created by cathym on 2015/5/26.
 */

import org.apache.commons.cli.*;
import org.apache.http.HttpEntity;
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


public class HTTPDemo{
    public static void main(String[] args) throws Exception {
        Options options = new Options();

        // ��� -h ����
        options.addOption("h", false, "Lists short help");

        // ��� -u ����
        options.addOption("u", true, "Set Username");

        // ��� -p ����
        options.addOption("p", true, "Set Password");

        // ��� -t ����
        options.addOption("t", true, "Sets the HTTP communication protocol for CIM connection");

        // ��� -URL ����
        options.addOption("URL", true, "Set URL");

        //get parameters
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("h")) {
            // ������ʾ��̵İ�����Ϣ
            System.out.println("help help");
        }

        //set http version
        String protocol = cmd.getOptionValue("t");
        ProtocolVersion httpVersion;
        if(protocol == null) {
            // ����Ĭ�ϵ� HTTP ����Э��
            httpVersion = HttpVersion.HTTP_1_0;
        } else {
            // �����û��Զ���� HTTP ����Э��
            if(protocol == "Http1.0")
                httpVersion = HttpVersion.HTTP_1_0;
            else
                httpVersion = HttpVersion.HTTP_1_1;
        }

        //set username and password
        String username = cmd.getOptionValue("u");
        String password = cmd.getOptionValue("p");


        //set URL
        String URL = cmd.getOptionValue("URL");

        for (String arg : args) {

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(URL); //"http://192.168.0.10:80"  https://iam-fed.juniper.net/auth/ilogin.html

            //set source IP
            RequestConfig config = RequestConfig.custom().setLocalAddress(InetAddress.getByName(arg)).build();
            httppost.setProtocolVersion(httpVersion);
            httppost.setConfig(config);

            StringEntity stringEntity = new StringEntity("hehe");
            stringEntity.setContentType("text/xml");
            stringEntity.setContentEncoding("UTF-8");
            httppost.setEntity(stringEntity);
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httppost.addHeader("username", username);
            httppost.addHeader("password", password);

            try {
                System.out.println("executing request " + httppost.getURI());
                CloseableHttpResponse response = httpclient.execute(httppost);
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {

                        System.out.println("--------------------------------------");
                        System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                        System.out.println("--------------------------------------");
                    }

                    //return the status code
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
                // close HttpClient
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}