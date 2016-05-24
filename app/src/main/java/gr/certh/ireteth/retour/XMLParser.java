package gr.certh.ireteth.retour;

/**
 * Created by dimitriskoutsaftikis on 11/13/15.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

public class XMLParser{

    ParserFinishedEventListener mListener;

    public interface ParserFinishedEventListener {
        void onEvent();
    }

    public void ParserFinishedEventListener(ParserFinishedEventListener eventListener) {
        mListener = eventListener;
    }

    public String xml;
    public void setXml(String xml){
        this.xml = xml;
    }


    // constructor
    public XMLParser() {

    }




    /**
     * Getting XML from URL making HTTP request
     *
     * @param url
     */


    public String getXmlFromUrl(String aurl) {

        if(android.os.Build.VERSION.SDK_INT>=11) {
            DownloadFileAsync downloadFileAsync = new DownloadFileAsync();
            downloadFileAsync.setDownLoadCompletedEventListener(new DownloadFileAsync.DownLoadCompletedEventListener() {
                @Override
                public void processFinish(String output) {
                    Log.d("XMLParser", "setDownLoadCompletedEventListener:" + output);
                    setXml(output);
                    if (mListener!=null){
                        mListener.onEvent();
                    }
                }
            });
            downloadFileAsync.execute(aurl);
        }
        else {
            StringBuilder result = new StringBuilder();

            try {

                URL url = new URL(aurl);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lengthOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("XMLParse", "downloadXml:" + result.toString());
            xml = result.toString();
            return result.toString();
        }

        Log.d("XMLParse","getXmlFromUrl:"+xml);

        // return XML
        return xml;
    }

    /**
     * Getting XML DOM element
     *
     * @param XML string
     */
    public Document getDomElement(String xml) {
        Document doc = null;
        if (xml == null){
            return doc;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    /**
     * Getting node value
     *
     * @param elem
     */
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Getting node value
     *
     * @param Element
     * @param key
     */
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}