package com.kjsce.meshde.menews;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.R.id.list;

//http://timesofindia.indiatimes.com/rssfeedstopstories.cms
public class News extends Activity {

    private HashMap<String,String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        createMap();
        String url;
        try{
            String type = getIntent().getExtras().getString("type");
            url = map.get(type);
        } catch (Exception e){
            url = "http://timesofindia.indiatimes.com/rssfeedstopstories.cms";
        }

        //String url = "http://timesofindia.indiatimes.com/rssfeedstopstories.cms";
        View view = findViewById(android.R.id.content);
        new fetchFeeds(getBaseContext(), view).execute(url);
        //System.out.println("What about now?");
        //System.out.println(view.findViewById(R.id.all) == findViewById(R.id.all));
    }
    public void createMap(){
        map = new HashMap<String,String>();
        map.put("Top","http://timesofindia.indiatimes.com/rssfeedstopstories.cms");
        map.put("India","http://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms");
        map.put("World","http://timesofindia.indiatimes.com/rssfeeds/296589292.cms");
        map.put("Sports","http://timesofindia.indiatimes.com/rssfeeds/4719148.cms");
        map.put("Business","http://timesofindia.indiatimes.com/rssfeeds/1898055.cms");
        map.put("Education","http://timesofindia.indiatimes.com/rssfeeds/913168846.cms");
    }
}
class fetchFeeds extends AsyncTask<String,Void,TripleArrayList>{

    private Context context;
    private View view;
    private TripleArrayList news;
    fetchFeeds(Context c,View v){
        context = c;
        view = v;
    }
    @Override
    protected TripleArrayList doInBackground(String... x) {
        ArrayList<String> headlines = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        try {
            URL url = new URL(x[0]);
            System.out.println(url);
            XmlPullParserFactory factory  = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(getInputStream(url),"UTF_8");

            int eventType = parser.getEventType();
            int state = 0;
            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    if(parser.getName().equalsIgnoreCase("item")){
                        state = 1;
                    }
                    else if(parser.getName().equalsIgnoreCase("title") && state == 1){
                        String z = parser.nextText();
                        System.out.println(z);
                        headlines.add(z);
                    }
                    else if(parser.getName().equalsIgnoreCase("link") && state == 1){
                        String z = parser.nextText();
                        System.out.println(z);
                        links.add(z);
                    }
                    else if(parser.getName().equalsIgnoreCase("description") && state == 1){
                        String z = parser.nextText();
                        System.out.println(z);
                        descriptions.add(z);
                    }
                }
                else if(eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")){
                    state = 0;
                }
                eventType = parser.next();
            }
            //System.out.println("Size of News is "+ news.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        TripleArrayList news = new TripleArrayList(headlines,links,descriptions);
        return news;
    }
    protected InputStream getInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            //System.out.println("Heereee meshdeeee "+e);
            return null;
        }
    }
    protected void onPostExecute(TripleArrayList n){
        //System.out.println("Size of News NOW is "+ news.size());
        news = n;
        ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,news.getHeadlines());
        //System.out.println("Here I am Motherfucker");
        ListView ls = (ListView) view.findViewById(android.R.id.list);
        //System.out.println("No,Here I am Motherfucking Asshole");
        ls.setAdapter(adapter);
        //System.out.println("But this is where I really am fool!");
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = news.getLinks().get(position);
                Uri url = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW,url);
                context.startActivity(intent);
            }
        });
    }
}

class TripleArrayList {
    private ArrayList<String> headlines;
    private ArrayList<String> links;
    private ArrayList<String> description;

    TripleArrayList(ArrayList<String> h, ArrayList<String> l, ArrayList<String> d) {
        headlines = h;
        links = l;
        description = d;
    }

    public ArrayList<String> getHeadlines() {
        return headlines;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public ArrayList<String> getDescription() {
        return description;
    }
}
