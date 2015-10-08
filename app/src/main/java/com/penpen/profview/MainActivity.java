package com.penpen.profview;

import adapter.FeedListAdapter;
import app.AppController;
import data.FeedItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockFragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    //https://api.vk.com/method/wall.get?owner_id=-53393178&filter=all&count=100
    private String URL_FEED = "https://api.vk.com/method/wall.get?owner_id=-53393178&filter=all&count=100";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);

        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sidemenu);
        menu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);

        String[] items = {"Новости","Подать достижение", "Настройки"};
        ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                new ArrayAdapter<Object>(
                        this,
                        R.layout.sidemenu_item,
                        R.id.text,
                        items
                )
        );

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
      //  getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
  //      getActionBar().setIcon(
     //           new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }

    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("response");
            VolleyLog.d(TAG, feedArray.toString());
            for (int i = 1; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                VolleyLog.d(TAG, i);
                VolleyLog.d(TAG, "Error: " + feedArray.get(i).toString());
                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName("Разное");
                
                if (feedObj.isNull("attachment") == false) {
                    /// Image might be null sometimes
                    String image = feedObj.getJSONObject("attachment").isNull("photo") ? null : feedObj
                            .getJSONObject("attachment").getJSONObject("photo").getString("src_big");
                    item.setImge(image);
                    // url might be null sometimes
                    String feedUrl = feedObj.getJSONObject("attachment").isNull("link") ? null : feedObj
                            .getJSONObject("attachment").getJSONObject("link").getString("url");
                    item.setUrl(feedUrl);

                }
                item.setStatus(feedObj.getString("text"));
                item.setProfilePic("https://pp.vk.me/c410124/v410124933/a3fa/SF8mkyWprrY.jpg");
                item.setTimeStamp(feedObj.getString("date"));


                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
}
