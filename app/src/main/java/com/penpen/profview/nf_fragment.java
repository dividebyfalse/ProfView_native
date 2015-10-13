package com.penpen.profview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
public abstract class nf_fragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private ProgressDialog progressDialog;
    public static int lay;
    public static int lvid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(lay, container, false);
        listView = (ListView) rootView.findViewById(lvid);
        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        progressDialog = ProgressDialog.show(getActivity(), "", "Загрузка. Подождите...", true);
        SharedPreferences settings = getActivity().getSharedPreferences("temp", 0);
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(getURL());
        if (settings.getString("my", "") == "test") {
            if (entry != null) {
              /*  try {
                    String data = new String(entry.data, "UTF-8");*/
                   // try {
                       // parseJsonFeed(new JSONObject(data));
                        new ParseTask().execute("");
                  /*  } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
            }
        } else {
            if (isOnline()) {
             /*   JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                        getURL(), null, new Response.Listener<JSONObject>() {
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
                        Entry entry = cache.get(getURL());
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
                });*/

               // AppController.getInstance().addToRequestQueue(jsonReq);
                new ParseTask().execute("");
            }
        }
        progressDialog.dismiss();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getActivity().getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "test");
        editor.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true;
        }
        else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Проверьте интернет соединение", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    protected abstract String getURL();

    public class ParseTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            if (params[0] != "") {
               String Url = params[0];
            }
            try {
                URL url = new URL(getURL());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);
                try {
                    feedItems.clear();
                    JSONArray feedArray = dataJsonObj.getJSONArray("response");
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}