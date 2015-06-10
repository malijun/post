package test1;

/**
 * Created by cathym on 2015/6/4.
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.apache.http.client.utils.HttpClientUtils.closeQuietly;


class NewThread implements Runnable{

    static int numberOfSuccess = 0;

    String username = null;
    String password = null;
    String destIP[] = null;
    String destURL  = null;
    String sourceIP = null;
    ProtocolVersion httpVersion = HttpVersion.HTTP_1_0;  //HttpVersion.HTTP_1_1
    Thread t;
    String XMLContent = null; // XMLContent
    CloseableHttpClient httpclient = null;



    NewThread(String usernameT, String passwordT, String destIPT, ProtocolVersion httpVersionT, String sourceIPT,String XMLContentT,CloseableHttpClient httpclientT) {
        // 创建新线程
        username = usernameT;
        password = passwordT;
        destURL = destIPT;
        destIP = destIPT.split("/");
        //System.out.println("destIP[0]   "+(destIP[0].equals("https:")));

        httpVersion = httpVersionT;
        sourceIP = sourceIPT;
        XMLContent = XMLContentT;
        httpclient = httpclientT;

        //System.out.println(XMLContent);
        t = new Thread(this, "Demo Thread");
        t.start(); // 开始线程
    }

    @Override
    public void run() {

        //RIGHT PART
        HttpPost httppost = new HttpPost(destURL); //https://iam-fed.juniper.net/auth/ilogin.html
        RequestConfig config = null;
        try {
            config = RequestConfig.custom()
                    .setLocalAddress(InetAddress.getByName(sourceIP))
                    .setConnectTimeout(2000)
                    .build();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        httppost.setProtocolVersion(httpVersion);
        httppost.setConfig(config);
        httppost.setHeader("connection","keep-alive");//new!!!

        StringEntity stringEntity = new StringEntity(XMLContent,"UTF-8");

        stringEntity.setContentType("application/xml");
        httppost.setEntity(stringEntity);

        httppost.setHeader("Content-Type", "text/xml");//("Content-Type", "application/x-www-form-urlencoded");

        try {
            System.out.println("executing request " + httppost.getURI());

            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                System.out.println(" execute!");
                System.out.println(response.getStatusLine());

                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    numberOfSuccess++;
                } else if (code == 400) {
                    //Format issue. Stop this thread and exam the format
                    System.out.println("XML Format issue");
                    //exam your format
                    System.out.println(httppost.getEntity());
                    //response = httpclient.execute(httppost);
                } else if (code == 503) {
                    //Busy. Wait some time and retry every post
                    System.out.println("Server busy");
                    Thread.sleep(2000);//wait and retry
                    response = httpclient.execute(httppost);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
//                    httppost.releaseConnection();//
//                    System.out.println(" close!");
                    if (response != null) {
                        closeQuietly(response);
                    }
                    HTTPPost.postInLoop --;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

class XMLContent{
    static String GetEntries(int num, String url){

        Element entries = null;
        String XMLStr = null;

        try {
            File f = new File(url);

            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            Element root = doc.getRootElement();

            List<Element> infos = root.elements("userfw-entries");

            if(infos.size() > 0){
                num = num % infos.size();
                entries = infos.get(num);
            }

            String e = entries.asXML();
            Document document = DocumentHelper.parseText(e);

            XMLStr = document.asXML();
            //System.out.println(XMLStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return XMLStr;
    }
}

public class HTTPPost{

    static int postInLoop = 0;

    public static void main(String[] args) throws Exception {

        Options options = new Options();

        // 添加 -postNumber 参数
        options.addOption("postNumber", true, "postNumber");

        // 添加 -timeInterval 参数
        options.addOption("timeInterval", true, "timeInterval");

        // 添加 -sourceIP 参数
        options.addOption("sourceIP",true,"Set source IPs eg: 192.168.0.10  192.138.0.15  192.168.0.27");

        // 添加 -h 参数
        options.addOption("h", false, "Lists short help");

        // 添加 -u 参数
        options.addOption("u", true, "Set Username");

        // 添加 -p 参数
        options.addOption("p", true, "Set Password");

        // 添加 -t 参数
        options.addOption("t", true, "Set the HTTP communication protocol for CIM connection eg: http1.0");

        // 添加 -e 参数
        options.addOption("e", true, "Set the XML file address which contains lots of entries eg: C:\\Users\\cathym\\Documents\\perl\\haha.xml");

        // 添加 -destURL 参数
        options.addOption("destURL", true, "Set destination URL eg: http://192.168.0.10:80");

        //get parameters
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("h")) {
            // 这里显示简短的帮助信息
            System.out.println("help help");
            //System.out.println("example:/n -u username /n -p password /n ");
        }

        //set http version
        String protocol = cmd.getOptionValue("t");

        // 设置默认的 HTTP 传输协议
        ProtocolVersion httpVersion = HttpVersion.HTTP_1_1;
        if(protocol.equals("http1.0")){
            httpVersion = HttpVersion.HTTP_1_0;
        }


        //set username and password
        String username = cmd.getOptionValue("u");
        String password = cmd.getOptionValue("p");


        //set entriesFile address
        String entriesFile = cmd.getOptionValue("e");

        //set destURL
        String destURL = cmd.getOptionValue("destURL");
        String destIP[] = destURL.split("/");

        int postNumber = Integer.parseInt(cmd.getOptionValue("postNumber"));

        int timeInterval = Integer.parseInt(cmd.getOptionValue("timeInterval"));

        ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 10000, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.DiscardOldestPolicy());
        /**
         * 第一参数：指的是保留的线程池大小。
         * 第二参数：指的是线程池的最大大小。
         * 第三参数：线程池维护线程所允许的空闲时间。
         * 第四参数： 线程池维护线程所允许的空闲时间的单位。
         * 第五参数： 表示存放任务的队列。　
         * 第六参数： 线程池对拒绝任务的处理策略，默认值ThreadPoolExecutor.AbortPolicy()。
         */

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        CloseableHttpClient httpclient = null;
//        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
//        CloseableHttpClient httpclient =  null;
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

//        MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();

        if(destIP[0].equals("https:")){
            String[] ip = destIP[2].split(":");
            int port = 443;
            if(ip.length>1){
                port = Integer.parseInt(ip[1]);
            }
            credsProvider.setCredentials(
                    new AuthScope(ip[0],port),
                    new UsernamePasswordCredentials(username, password));
//            httpclient.getState().setCredentials(new AuthScope(ip[0], port, AuthScope.ANY_REALM), defaultcreds);

            try{
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                httpclient = HttpClients.custom()
                        .setDefaultCredentialsProvider(credsProvider)
//                        .setCredentials(new AuthScope(ip[0], port, AuthScope.ANY_REALM), defaultcreds)
                        .setSSLSocketFactory(sslsf)
                        .setConnectionManager(cm)
                        .build();
//                httpclient.getParams().setAuthenticationPreemptive(true);
            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            String[] ip = destIP[2].split(":");
            int port = 80;
            if(ip.length>1){
                port = Integer.parseInt(ip[1]);
            }
            credsProvider.setCredentials(
                    new AuthScope(ip[0], port),//"10.208.128.232"
                    new UsernamePasswordCredentials(username, password));
            try{
                httpclient = HttpClients.custom()
                        .setDefaultCredentialsProvider(credsProvider)
                        .setConnectionManager(cm)
//                        .setConnectionTimeToLive(5, TimeUnit.SECONDS)
                        .build();
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        List<Element> entries = GetAllEntries(entriesFile);

        if(!cmd.hasOption("sourceIP")){

            while(postNumber>0){
                if(postInLoop == 0){
                    postInLoop = 20;
                    for(int i = 0; i<20 ; i++){
                        System.out.println("postNumber"+postNumber);
                        postNumber --;
                        pool.execute(new NewThread(username,password,destURL,httpVersion, InetAddress.getLocalHost().getHostAddress(),GetOneEntries(entries,postNumber),httpclient));
                    }
                }
            }
            pool.shutdown();
            System.out.println("numberOfSuccessPost : "+NewThread.numberOfSuccess);

//            for(int i = 0; i<postNumber; i++){
//
//                pool.execute(new NewThread(username,password,destURL,httpVersion, InetAddress.getLocalHost().getHostAddress(),GetOneEntries(entries,i),httpclient));
//                //new NewThread(username,password,destURL,httpVersion,InetAddress.getLocalHost().getHostAddress().toString(),GetOneEntries(entries,i),httpclient);
//                Thread.sleep(timeInterval*1000);
//                System.out.println(i);
//            }
//            //pool.shutdown();
//            System.out.println("numberOfSuccessPost : "+NewThread.numberOfSuccess);
        }else{
            String[] sourceIPs = cmd.getOptionValues("sourceIP");
            for(int i=0;i<sourceIPs.length;i++){
                pool.execute(new NewThread(username,password,destURL,httpVersion,sourceIPs[i],GetOneEntries(entries,i),httpclient));
            }
            pool.shutdown();
            System.out.println("numberOfSuccessPost : " + NewThread.numberOfSuccess);
        }

//        try {
//            httpclient.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    static List<Element> GetAllEntries(String url){

        List<Element> infos = null;

        try {
            File f = new File(url);

            SAXReader reader = new SAXReader();
            Document doc = reader.read(f);
            Element root = doc.getRootElement();

            infos = root.elements("userfw-entries");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    static String GetOneEntries(List<Element> infos, int num){
        String XMLStr = null;
        Element entries;
        try{
            num = num % infos.size();
            entries = infos.get(num);
            String e = entries.asXML();
            Document document = DocumentHelper.parseText(e);

            XMLStr = document.asXML();
        }catch(Exception e){
            e.printStackTrace();
        }
        return XMLStr;
    }

}