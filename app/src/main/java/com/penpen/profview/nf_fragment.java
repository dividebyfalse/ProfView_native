package com.penpen.profview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.FeedListAdapter;
import app.authorization;
import data.FeedItem;

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
    boolean is_ref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout eelay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getlay(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) getActivity().findViewById(getlvid());
        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
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
                        new ParseTask().execute(lss);
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
                    new ParseTask().execute(lss);
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

   /* @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getActivity().getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "test");
        editor.commit();
    }*/
    protected abstract int getlvid();

    protected abstract int getlayid();

    protected abstract String getee();

    protected abstract int getlay();

    protected abstract List<String> getURL();

    public class ParseTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        private String urlgroupsimages = "";
        private String profimg ="";
        private  String profname = "";

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
                    //if (fieldType.equals(Date.class)) {
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H:m:s");
                        newInstance_valA = Integer.parseInt(valA);// dateFormat.parse(valA);
                        newInstance_valB = Integer.parseInt(valB);// dateFormat.parse(valB);
                    /*} else {
                        newInstance_valA = constructor.newInstance(valA);
                        newInstance_valB = constructor.newInstance(valB);
                    }*/
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
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
            if (params.length == 1) {
                JSONObject dataJsonObj = null;
                String[] url = params[0].split(";");
                if (url.length>1) {
                    List<JSONObject> jlist = new ArrayList<>();
                    urlgroupsimages = "{\"groups\":{";
                    for (int i = 0; i < url.length; i++) {
                        try {
                            JSONObject dataJson = new JSONObject(getJSON(url[i]+"&count=1&extended=1"));
                            JSONObject response = dataJson.getJSONObject("response");
                            JSONArray groups = response.getJSONArray("groups");
                            for (int k=0; k<groups.length(); k++) {
                                if (groups.getJSONObject(k).getString("gid").equals(url[i].substring(url[i].indexOf("owner_id=-") + 10, url[i].indexOf("&", url[i].indexOf("owner_id=-"))))) {
                                    urlgroupsimages+="\""+groups.getJSONObject(k).getString("gid")+"\":{\"name\":\""+groups.getJSONObject(k).getString("name")+"\",\"photo\":\""+groups.getJSONObject(k).getString("photo_medium")+"\"},";
                                    break;
                                }
                            }
                            dataJsonObj = new JSONObject(getJSON(url[i]+"&count="+String.valueOf(Math.round(100/url.length))));
                            JSONArray feedArray = dataJsonObj.getJSONArray("response");
                            for (int j=1;j<feedArray.length(); j++) {
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
                    resultJson = getJSON(url[0]);
                    try {
                        JSONObject dataJson = new JSONObject(getJSON(url[0].substring(0, url[0].length()-10) + "&count=1&extended=1"));
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
                    JSONObject groups = null;
                    if (urlgroupsimages != "") {
                        groups = new JSONObject(urlgroupsimages).getJSONObject("groups");
                    }
                    for (int i = 1; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        FeedItem item = new FeedItem();
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
                        if (groups != null) {
                            item.setProfilePic(groups.getJSONObject(feedObj.getString("to_id").substring(1)).getString("photo").replace("\\", ""));
                        } else {
                            if (profimg.length() != 0) {
                                item.setProfilePic(profimg);
                            } else {
                                item.setProfilePic("https://pp.vk.me/c410124/v410124933/a3fa/SF8mkyWprrY.jpg");
                            }
                        }
                        item.setNewsid(feedObj.getString("to_id")+"_"+feedObj.getString("id"));
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
