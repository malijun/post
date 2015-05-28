package test1;

/**
 * Created by cathym on 2015/5/26.
 */

import org.apache.commons.cli.*;
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
        Options options = new Options();

        // Ìí¼Ó -h ²ÎÊý
        options.addOption("h", false, "Lists short help");

        // Ìí¼Ó -t ²ÎÊý
        options.addOption("t", true, "Sets the HTTP communication protocol for CIM connection");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("h")) {
            // ÕâÀïÏÔÊ¾¼ò¶ÌµÄ°ïÖúÐÅÏ¢
        }
        String protocol = cmd.getOptionValue("t");

        if(protocol == null) {
            // ÉèÖÃÄ¬ÈÏµÄ HTTP ´«ÊäÐ­Òé
        } else {
            // ÉèÖÃÓÃ»§×Ô¶¨ÒåµÄ HTTP ´«ÊäÐ­Òé
        }
        String username = "username";
        String password = "password";
        ProtocolVersion httpVersion = HttpVersion.HTTP_1_0;
        if(protocol=="Http1.0")
            httpVersion = HttpVersion.HTTP_1_0;
        else
            httpVersion = HttpVersion.HTTP_1_1;
//        HttpHost proxy = new HttpHost("10.108.147.241", 80);
//        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//        CloseableHttpClient httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();

        for (String arg : args) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://192.168.0.10:80"); //https://iam-fed.juniper.net/auth/ilogin.html
            //ï¿½ï¿½ï¿½ï¿½http version
            RequestConfig config = RequestConfig.custom().setLocalAddress(InetAddress.getByName(arg)).build();
            httppost.setProtocolVersion(httpVersion);
            httppost.setConfig(config);

            StringEntity stringEntity = new StringEntity("hehe");
            stringEntity.setContentType("application/xml");
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
                // ï¿½Ø±ï¿½ï¿½ï¿½ï¿½ï¿½,ï¿½Í·ï¿½ï¿½ï¿½Ô´
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // ï¿½ï¿½ï¿½ï¿½httppost,ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿?httpsï¿½ï¿½Õ¾Ò²ï¿½Ç¿ï¿½ï¿½Ôµï¿½


    }
}