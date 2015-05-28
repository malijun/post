package test1;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;


/**
 * Created by cathym on 2015/5/26.
 */

public class HTTPSDemo {

    private static final String SERVER = "https://xxx.com";

    public static void main(String[] args) {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = HTTPSDemo.class.getResourceAsStream("xxx.keystore");
            trustStore.load(in, "123456".toCharArray());
            in.close();
            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
            Scheme sch = new Scheme("https", 443, socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);

            HttpPost httpPost = new HttpPost(SERVER);
            System.out.println("executing request" + httpPost.getRequestLine());
            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);// 获得响应实体
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            System.out.println(entity);
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
            }
            // 销毁实体
            EntityUtils.consume(entity);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {
            e.printStackTrace();
        } finally {
            // 当不再需要HttpClient实例时,关闭连接管理器以确保释放所有占用的系统资源
            httpClient.getConnectionManager().shutdown();
        }
    }

}