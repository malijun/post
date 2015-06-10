package test1;

/**
 * Created by cathym on 2015/6/5.
 */

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class GetXMLContent {

    static String GetEntries(int num, String url){

        Element entries = null;
        String XMLStr = null;

        try {
            File f = new File("C:\\Users\\cathym\\Documents\\haha.xml");

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
            System.out.println(XMLStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return XMLStr;
    }
}