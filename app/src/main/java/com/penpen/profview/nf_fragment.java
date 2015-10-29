package com.penpen.profview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static int lay;
    public static int lvid;
    public static int layid;
    boolean is_ref;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(lay, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) getActivity().findViewById(lvid);
        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(layid);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    is_ref = true;
                    List<String> ls = getURL();
                    String lss="";
                    if (ls.size()>0) {
                        for (int i = 0; i < ls.size() ; i++) {
                            lss += ls.get(i) + ";";
                        }
                        lss=lss.substring(0, lss.length()-1);
                    }
                    new ParseTask().execute(lss);
                    is_ref = false;
                }
            }
        });
        //SharedPreferences settings = getActivity().getSharedPreferences("temp", 0);
        /*Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(getURL());*/
        /*if (settings.getString("my", "") == "tes") {
            if (entry != null) {
                try {
                    String data = new String(entry.data, "UTF-8");
                new ParseTask().execute(data);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                if (isOnline()) {
                    new ParseTask().execute("");
                }
            }
        } else {*/
            if (isOnline()) {
                List<String> ls = getURL();
                String lss="";
                if (ls.size()>0) {
                    for (int i = 0; i < ls.size(); i++) {
                        lss += ls.get(i) + ";";
                    }
                    lss=lss.substring(0, lss.length()-1);
                }
                new ParseTask().execute(lss);
            } /*else {
                if (entry != null) {
                    try {
                        String data = new String(entry.data, "UTF-8");
                        new ParseTask().execute(data);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }*/
        //}
    }

   /* @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getActivity().getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "test");
        editor.commit();
    }*/

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true;
        }
        else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Проверьте интернет соединение", Toast.LENGTH_SHORT);
            toast.show();
            swipeRefreshLayout.setRefreshing(false);
            return false;
        }
    }

    protected abstract List<String> getURL();

    public class ParseTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (is_ref == false) {
                swipeRefreshLayout.setProgressViewOffset(false, 0,
                        (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                24,
                                getResources().getDisplayMetrics()));
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        private String getJSON(String URL) {
            String JSON = "";
            try {
                URL url = new URL(URL);
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
                JSON = buffer.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            return JSON;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 1) {
                JSONObject dataJsonObj = null;
                int al=0;
                //List<String> JSONList= new ArrayList<String>();;
                String[] url = params[0].split(";");
                if (url.length>1) {
                    String cs = "";
                    for (int i = 0; i < url.length; i++) {
                        try {
                            dataJsonObj = new JSONObject(getJSON(url[i]));
                            JSONArray feedArray = dataJsonObj.getJSONArray("response");
                            al += feedArray.length();
                            for (int j = 1; j < feedArray.length(); j++) {
                                JSONObject feedObj = (JSONObject) feedArray.get(j);
                                cs += feedObj.toString() + ",";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    cs = cs.substring(0, cs.length() - 1);
                    cs = "{\"response\":[162," + cs + "]}";
                    try {
                        dataJsonObj = new JSONObject(cs);
                        JSONArray feedArray = dataJsonObj.getJSONArray("response");
                        Log.d("fal", String.valueOf(al));
                        String[] ls;
                        ls = feedArray.join(";sdf4s6df4d2sf;").split(";sdf4s6df4d2sf;");
                        for (int i = 1; i < feedArray.length() - 1; i++) {
                            for (int j = i; j < feedArray.length() - 1; j++) {
                                JSONObject feedObjo = (JSONObject) feedArray.get(i);
                                JSONObject feedObjt = (JSONObject) feedArray.get(j);
                                Date sd = new java.util.Date(Long.valueOf(feedObjo.getString("date")) * 1000);
                                Date ed = new java.util.Date(Long.valueOf(feedObjt.getString("date")) * 1000);
                                if (ed.after(sd)) {
                                    String a = ls[i];
                                    ls[i] = ls[j];
                                    ls[j] = a;
                                }
                            }
                        }
                        cs = "";
                        Log.d("b", String.valueOf(ls.length));
                        for (int i = 0; i < ls.length - 1; i++) {
                            cs += ls[i] + ",";
                        }
                        cs = cs.substring(0, cs.length() - 1);
                        cs = "{\"response\":[" + cs + "]}";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    resultJson = cs;
                } else {
                    resultJson = getJSON(url[0]);
                }
                //todo:соединение json
            } else {
                resultJson = params[1];
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
                    //VolleyLog.d(TAG, feedArray.toString());
                    for (int i = 1; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        /*VolleyLog.d(TAG, i);
                        VolleyLog.d(TAG, "Error: " + feedArray.get(i).toString());*/
                        FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        item.setName("Разное");
                        if (feedObj.isNull("attachment") == false) {
                            String image = feedObj.getJSONObject("attachment").isNull("photo") ? null : feedObj
                                    .getJSONObject("attachment").getJSONObject("photo").getString("src_big");
                            item.setImge(image);
                            if (feedObj.getJSONArray("attachments") != null) {
                                for (int j=0; j<feedObj.getJSONArray("attachments").length(); j++) {
                                    if (feedObj.getJSONArray("attachments").getJSONObject(j).getString("type").contentEquals("link")) {
                                        item.setUrl(feedObj.getJSONArray("attachments").getJSONObject(j).getJSONObject("link").getString("url"));
                                        break;
                                    }
                                }
                            }
                        }
                        item.setStatus(Html.fromHtml(feedObj.getString("text")).toString());
                        item.setProfilePic("https://pp.vk.me/c410124/v410124933/a3fa/SF8mkyWprrY.jpg");
                        item.setTimeStamp(feedObj.getString("date")+"000");
                        feedItems.add(item);
                    }
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
