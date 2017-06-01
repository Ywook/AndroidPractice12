package com.examples.androidpractice12;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main2Activity extends AppCompatActivity {
    ArrayList<String> data = new ArrayList<>();

    ArrayAdapter adapter ;
    ListView listView;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = (ListView)findViewById(R.id.listview);
        btn = (Button)findViewById(R.id.btn);

        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, data);

        listView.setAdapter(adapter);
    }

    public  void onClick2(View v){
        if(v.getId()== R.id.btn){
            new mThread().start();
        }else{
            Intent intent = new Intent(Main2Activity.this, Main3Activity.class);
            startActivity(intent);
        }
    }
    class  mThread extends Thread{
        @Override
        public void run() {
            try {
                URL url = new URL("https://news.google.com/news?cf=all&hl=ko&pz=1&ned=kr&topic=m&output=rss");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    int itemCount = readData(urlConnection.getInputStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (SAXException e) {
                e.printStackTrace();
            }

        }
    }
    Handler handler = new Handler();

    public int readData(InputStream is) throws IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(is);

            int datacount = parseDocument(document);
            return datacount;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private  int parseDocument(Document doc){
        Element docEle = doc.getDocumentElement();
        NodeList nodeList = docEle.getElementsByTagName("item");
        int count = 0;

        if((nodeList != null) && (nodeList.getLength()> 0)){
            for(int i = 0 ; i < nodeList.getLength(); i++){
                String newsItem = getTagData(nodeList, i);
                if(newsItem != null){
                    data.add(newsItem);
                    count++;
                }
            }
        }
        return count;
    }

    private String getTagData(NodeList nodelist, int index) {
        String newsItem = null;
        try {
            Element entry = (Element) nodelist.item(index);
            Element title = (Element) entry.getElementsByTagName("title").item(0);
            Element pubDate = (Element) entry.getElementsByTagName("pubDate").item(0);

            String titleValue = null;
            if (title != null) {
                Node firstChild = title.getFirstChild();
                if (firstChild != null) titleValue = firstChild.getNodeValue();
            }
            String pubDateValue = null;
            if(pubDate != null){
                Node firstChild = pubDate.getFirstChild();
                if(firstChild != null) pubDateValue = firstChild.getNodeValue();
            }
            Log.d("dateParse", pubDateValue);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            newsItem = titleValue + "-" + simpleDateFormat.format(date.parse(pubDateValue));
            Log.d("dateParse", newsItem);
        } catch (DOMException e) {
            e.printStackTrace();
        }
        return newsItem;
    }

}
