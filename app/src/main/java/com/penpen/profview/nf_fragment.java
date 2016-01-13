package com.penpen.profview;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FeedListAdapter;
import app.AppController;
import app.authorization;
import data.FeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by penpen on 13.10.15.
 */
public abstract class nf_fragment extends Fragment {

    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    boolean is_ref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout eelay;
    private SharedPreferences settings;
    private Boolean flag_loading;
    private int offset;
    private MainActivity ma;


    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    private String urlgroupsimages = "";
    private String profimg ="";
    private  String profname = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getlay(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ma = (MainActivity) getActivity();
        flag_loading = false;
        offset = 0;
        ListView listView = (ListView) getActivity().findViewById(getlvid());
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (!flag_loading) {
                        offset+=100;
                        flag_loading = true;
                        if (authorization.isOnline(getActivity())) {
                            swipeRefreshLayout.setRefreshing(true);
                            is_ref = true;
                            List<String> ls = getURL();
                            String lss="";
                            if (ls.size()>0) {
                                for (int i = 0; i < ls.size() ; i++) {
                                    lss += ls.get(i) + ";";
                                }
                                lss=lss.substring(0, lss.length()-1);
                                ma.pt = new ParseTask().execute(lss, String.valueOf(offset));
                            } else {
                                TextView eet = (TextView) eelay.findViewById(R.id.eet);
                                eet.setText(getee());
                                eelay.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                            }
                            is_ref = false;
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            }
        });

        eelay = (LinearLayout) view.findViewById(R.id.eel);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(getlayid());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (authorization.isOnline(getActivity())) {
                    is_ref = true;
                    List<String> ls = getURL();
                    String lss="";
                    if (ls.size()>0) {
                        for (int i = 0; i < ls.size() ; i++) {
                            lss += ls.get(i) + ";";
                        }
                        lss=lss.substring(0, lss.length()-1);
                        ma.pt = new ParseTask().execute(lss, String.valueOf(offset));
                    } else {
                        TextView eet = (TextView) eelay.findViewById(R.id.eet);
                        eet.setText(getee());
                        eelay.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.INVISIBLE);
                    }
                    is_ref = false;
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
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
            if (authorization.isOnline(getActivity())) {
                List<String> ls = getURL();
                String lss="";
                if (ls.size()>0) {
                    for (int i = 0; i < ls.size(); i++) {
                        lss += ls.get(i) + ";";
                    }
                    lss=lss.substring(0, lss.length()-1);
                    ma.pt = new ParseTask().execute(lss, String.valueOf(offset));
                    //at.cancel(true);
                } else {
                    TextView eet = (TextView) eelay.findViewById(R.id.eet);
                    eet.setText(getee());
                    eelay.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
            /*else {
                if (entry != null) {
                    try {
                        String data = new String(entry.data, "UTF-8");
                        new ParseTask().execute(data);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }*/
            }
        //}
    }

    protected abstract int getlvid();

    protected abstract int getlayid();

    protected abstract String getee();

    protected abstract int getlay();

    protected abstract List<String> getURL();

    public class JsonObjectComparator implements Comparator<JSONObject> {
        private final String fieldName;
        private Class<? extends Comparable> fieldType;

        public JsonObjectComparator(String fieldName, Class<? extends Comparable> fieldType) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        @Override
        public int compare(JSONObject a, JSONObject b) {
            String valA, valB;
            Comparable newInstance_valA, newInstance_valB;
            int comp = 0;
            try {
                Constructor<? extends Comparable> constructor = fieldType.getConstructor(String.class);
                valA = a.getString(fieldName);
                valB = b.getString(fieldName);
                newInstance_valA = Integer.parseInt(valA);// dateFormat.parse(valA);
                newInstance_valB = Integer.parseInt(valB);// dateFormat.parse(valB);
                comp = newInstance_valA.compareTo(newInstance_valB);
            } catch (Exception e) {
                Log.d("sort json error", e.getMessage());
            }

            if(comp > 0)
                return -1;
            if(comp < 0)
                return 1;
            return 0;
        }
    }

    public class ParseTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        private String urlgroupsimages = "";
        private String profimg ="";
        private  String profname = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!is_ref) {
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
                Log.d("url", URL);
                URL url = new URL(URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSON = buffer.toString();
                urlConnection.disconnect();
            }catch(Exception e){
                e.printStackTrace();
            }
            return JSON;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 2) {
                JSONObject dataJsonObj;
                String[] url = params[0].split(";");
                if (url.length>1) {
                    List<JSONObject> jlist = new ArrayList<>();
                    urlgroupsimages = "{\"groups\":{";
                    for (String anUrl : url) {
                        try {
                            JSONObject dataJson = new JSONObject(getJSON(anUrl + "&count=1&extended=1&offset="+String.valueOf(offset)));
                            JSONObject response = dataJson.getJSONObject("response");
                            JSONArray groups = response.getJSONArray("groups");
                            for (int k = 0; k < groups.length(); k++) {
                                if (groups.getJSONObject(k).getString("gid").equals(anUrl.substring(anUrl.indexOf("owner_id=-") + 10, anUrl.indexOf("&", anUrl.indexOf("owner_id=-"))))) {
                                    urlgroupsimages += "\"" + groups.getJSONObject(k).getString("gid") + "\":{\"name\":\"" + groups.getJSONObject(k).getString("name") + "\",\"photo\":\"" + groups.getJSONObject(k).getString("photo_medium") + "\"},";
                                    break;
                                }
                            }
                            dataJsonObj = new JSONObject(getJSON(anUrl + "&count=" + String.valueOf(Math.round(100 / url.length))+"&offset="+String.valueOf(offset)));
                            JSONArray feedArray = dataJsonObj.getJSONArray("response");
                            for (int j = 1; j < feedArray.length(); j++) {
                                jlist.add(feedArray.getJSONObject(j));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    urlgroupsimages = urlgroupsimages.substring(0, urlgroupsimages.length()-1)+"}}";
                    JsonObjectComparator comparator = new JsonObjectComparator("date", Integer.class);
                    Collections.sort(jlist, comparator);
                    String ssum = "";
                    for (int i=0; i<jlist.size(); i++) {
                        if (i<jlist.size()-1) {
                            ssum += jlist.get(i).toString() + ",";
                        } else {
                            ssum += jlist.get(i).toString();
                        }
                    }
                    resultJson = "{\"response\":[2244," +ssum +"]}";
                } else if (url.length>0) {
                    resultJson = getJSON(url[0]+"&offset="+String.valueOf(offset));
                    try {
                        JSONObject dataJson = new JSONObject(getJSON(url[0].substring(0, url[0].length()-10) + "&count=1&extended=1&offset="+String.valueOf(offset)));
                        JSONObject response = dataJson.getJSONObject("response");
                        JSONArray groups = response.getJSONArray("groups");
                        for (int k=0; k<groups.length(); k++) {
                            if (groups.getJSONObject(k).getString("gid").equals(url[0].substring(url[0].indexOf("owner_id=-") + 10, url[0].indexOf("&", url[0].indexOf("owner_id=-"))))) {
                                profimg = groups.getJSONObject(k).getString("photo_medium");
                                profname = groups.getJSONObject(k).getString("name");
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                resultJson = params[1];
            }
            //************************
            JSONObject dataJsonObj;
            try {
                dataJsonObj = new JSONObject(resultJson);
                try {
                    if (is_ref) {
                        feedItems.clear();
                    }
                    JSONArray feedArray = dataJsonObj.getJSONArray("response");
                    JSONObject groups = null;
                    if (!urlgroupsimages.equals("")) {
                        groups = new JSONObject(urlgroupsimages).getJSONObject("groups");
                    }
                    for (int i = 1; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        final FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        if (groups != null) {
                            item.setName(groups.getJSONObject(feedObj.getString("to_id").substring(1)).getString("name"));
                        } else {
                            if (profname.length() !=0) {
                                item.setName(profname);
                            } else {
                                item.setName("Разное");
                            }
                        }
                        if (!feedObj.isNull("attachment")) {
                            if (feedObj.getJSONObject("attachment").has("video")) {
                               String JSON;
                                if (!settings.getString("VKAT", "").equals("") || !settings.getString("VKAT", "").equals("0")) {
                                    try {
                                        URL url = new URL("https://api.vk.com/method/video.get?videos=" + feedObj.getJSONObject("attachment").getJSONObject("video").getString("owner_id")+"_"+feedObj.getJSONObject("attachment").getJSONObject("video").getString("vid") + "&count=1&extended=0&version=5.44&access_token=" + settings.getString("VKAT", ""));
                                        urlConnection = (HttpURLConnection) url.openConnection();
                                        urlConnection.setRequestMethod("GET");
                                        urlConnection.setConnectTimeout(3000);
                                        urlConnection.connect();
                                        InputStream inputStream = urlConnection.getInputStream();
                                        StringBuilder buffer = new StringBuilder();
                                        reader = new BufferedReader(new InputStreamReader(inputStream));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            buffer.append(line);
                                        }
                                        JSON = buffer.toString();

                                        urlConnection.disconnect();
                                        try {
                                            JSONObject json = new JSONObject(JSON).getJSONArray("response").getJSONObject(1);
                                            Log.d("vj", json.toString());
                                            if (json.has("player")) {
                                                item.setVideoimg(json.getString("image_medium"));
                                                item.setExtvideo(json.getString("player"));
                                            }
                                        } catch (Exception ignored) {
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                               /* final VKRequest request = VKApi.video().get(VKParameters.from("videos", feedObj.getJSONObject("attachment").getJSONObject("video").getString("owner_id")+"_"+feedObj.getJSONObject("attachment").getJSONObject("video").getString("vid"), VKApiConst.COUNT, "1", VKApiConst.EXTENDED, "0", VKApiConst.VERSION, "5.42", VKApiConst.ACCESS_TOKEN, settings.getString("VKAT", "")));
                                request.executeWithListener(new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(VKResponse response) {
                                        //Do complete stuff
                                        try {
                                            JSONObject json = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(0);
                                            if (json.has("player") && json.getJSONObject("files").has("external")) {
                                                item.setExtvideo(json.getString("player"));
                                                extvideo = json.getString("player");
                                                Log.d("video", extvideo);
                                            } else if (json.has("files") && json.getJSONObject("files").has("mp4_360")) {
                                                video = json.getJSONObject("files").getString("mp4_360");
                                                item.setVideo(json.getJSONObject("files").getString("mp4_360"));
                                                Log.d("video", video);
                                            } else if (json.has("files") && json.getJSONObject("files").has("mp4_240")) {
                                                video = json.getJSONObject("files").getString("mp4_240");
                                                item.setVideo(json.getJSONObject("files").getString("mp4_240"));
                                                Log.d("video", video);
                                            }

                                        } catch (Exception ignored) {
                                        }
                                        Log.d("resp", response.responseString);
                                        //Log.d("video", video);
                                    }

                                    @Override
                                    public void onError(VKError error) {
                                        //Do error stuff
                                        Log.d("err", error.toString());
                                    }

                                    @Override
                                    public void attemptFailed(VKRequest request, int attemptNumber,
                                                              int totalAttempts) {
                                        //I don't really believe in progress
                                    }
                                });*/
                            }
                            JSONArray attachments = feedObj.getJSONArray("attachments");
                            ArrayList<String> photo = new ArrayList<>();
                            for (int k = 0; k<attachments.length(); k++) {
                                if (attachments.getJSONObject(k).getString("type").equals("photo")) {
                                    JSONObject photojson = attachments.getJSONObject(k).getJSONObject("photo");
                                    if (k<1) {
                                        if (photojson.has("src_big")) {
                                            photo.add(photojson.getString("src_big"));
                                        } else if (photojson.has("src")) {
                                            photo.add(photojson.getString("src"));
                                        } else if (photojson.has("src_small")) {
                                            photo.add(photojson.getString("src_small"));
                                        }
                                        //нормальное разрешение
                                        if (photojson.has("src_xxxbig")) {
                                            photo.add(photojson.getString("src_xxxbig"));
                                        } else if (photojson.has("src_xxbig")) {
                                            photo.add(photojson.getString("src_xxbig"));
                                        } else if (photojson.has("src_xbig")) {
                                            photo.add(photojson.getString("src_xbig"));
                                        } else if (photojson.has("src_big")) {
                                            photo.add(photojson.getString("src_big"));
                                        } else if (photojson.has("src")) {
                                            photo.add(photojson.getString("src"));
                                        } else if (photojson.has("src_small")) {
                                            photo.add(photojson.getString("src_small"));
                                        }
                                    } else {
                                        if (photojson.has("src_xxxbig")) {
                                            photo.add(photojson.getString("src_xxxbig"));
                                        } else if (photojson.has("src_xxbig")) {
                                            photo.add(photojson.getString("src_xxbig"));
                                        } else if (photojson.has("src_xbig")) {
                                            photo.add(photojson.getString("src_xbig"));
                                        } else if (photojson.has("src_big")) {
                                            photo.add(photojson.getString("src_big"));
                                        } else if (photojson.has("src")) {
                                            photo.add(photojson.getString("src"));
                                        } else if (photojson.has("src_small")) {
                                            photo.add(photojson.getString("src_small"));
                                        }
                                    }
                                }
                            }
                            item.setImage(photo);

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
                        if (groups != null) {
                            item.setProfilePic(groups.getJSONObject(feedObj.getString("to_id").substring(1)).getString("photo").replace("\\", ""));
                        } else {
                            if (profimg.length() != 0) {
                                item.setProfilePic(profimg);
                            } else {
                                item.setProfilePic("https://pp.vk.me/c410124/v410124933/a3fa/SF8mkyWprrY.jpg");
                            }
                        }
                        item.setNewsid(feedObj.getString("to_id") + "_" + feedObj.getString("id"));
                        item.setTimeStamp(feedObj.getString("date") + "000");
                        feedItems.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            if (strJson != null) {
                listAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                flag_loading = false;
                ma.pt = null;
            }
        }

        @Override
        protected void onCancelled(){
            // If you write your own implementation, do not call super.onCancelled(result).
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            swipeRefreshLayout.setRefreshing(false);
            super.onCancelled();
        }
    }
}
