package com.kjsce.meshde.menews;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import java.util.concurrent.ExecutionException;

import static android.R.id.list;

//http://timesofindia.indiatimes.com/rssfeedstopstories.cms
public class News extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        String url = "http://timesofindia.indiatimes.com/rssfeedstopstories.cms";
        View view = findViewById(android.R.id.content);
        new fetchFeeds(getBaseContext(),view).execute(url);
        //System.out.println("What about now?");
        //System.out.println(view.findViewById(R.id.all) == findViewById(R.id.all));
    }

}

class fetchFeeds extends AsyncTask<String,Void,ArrayList>{

    private Context context;
    private View view;
    fetchFeeds(Context c,View v){
        context = c;
        view = v;
    }
    @Override
    protected ArrayList doInBackground(String... x) {
        ArrayList<String> news = new ArrayList<>();
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
                        news.add(z);
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
    protected void onPostExecute(ArrayList news){
        //System.out.println("Size of News NOW is "+ news.size());
        ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,news);
        //System.out.println("Here I am Motherfucker");
        ListView ls = (ListView) view.findViewById(android.R.id.list);
        //System.out.println("No,Here I am Motherfucking Asshole");
        ls.setAdapter(adapter);
        //System.out.println("But this is where I really am fool!");
    }
}
