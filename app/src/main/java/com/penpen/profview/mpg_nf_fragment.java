package com.penpen.profview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import adapter.FeedListAdapter;
import app.AppController;
import data.FeedItem;


import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by penpen on 13.10.15.
 */
public class mpg_nf_fragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://api.vk.com/method/wall.get?owner_id=-53393178&filter=all&count=100";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mpg_feed, container, false);

        listView = (ListView) rootView.findViewById(R.id.list);
        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
/*
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

        } else {*/
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
                    Cache cache = AppController.getInstance().getRequestQueue().getCache();
                    Entry entry = cache.get(URL_FEED);
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
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
      //  }

        return rootView;
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
                item.setStatus(feedObj.getString("text").replace("<br>", ""));
                item.setProfilePic("https://pp.vk.me/c410124/v410124933/a3fa/SF8mkyWprrY.jpg");
                item.setTimeStamp(feedObj.getString("date")+"000");

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
