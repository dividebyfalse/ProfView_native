package com.penpen.profview;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.FeedListAdapter;
import app.authorization;
import data.FeedItem;

/**
 *
 * Created by penpen on 09.10.15.
 */
public class NewsFeed_fragment extends Fragment {
    public FragmentTabHost mTabHost;
    private LinearLayout prevsel;
    private int ptt;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    boolean is_ref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout eelay;
    private SharedPreferences settings;
    private Boolean flag_loading;
    private int offset;
    private MainActivity ma;
    private int tab;
    private String empty_feed_message;
    private NewsFeed_fragment nff;
    private String profimg;
    private String profname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.news_feed, container, false);
        nff = this;
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ma = (MainActivity) getActivity();
        flag_loading = false;
        offset = 0;
        tab = 1;
        ListView listView = (ListView) view.findViewById(R.id.listmpg);
        feedItems = new ArrayList<>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        eelay = (LinearLayout) view.findViewById(R.id.eel);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mpg);

        if (ma.tabstack.size() == 0) {
            ptt = 1;
            Log.d("dsf",String.valueOf(ptt));
        } else {
            ptt = ma.tabstack.get(ma.tabstack.size()-1);
            ma.tabstack.remove(ma.tabstack.size()-1);
            Log.d("ds",String.valueOf(ptt));
        }
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        ma.mTabHost = mTabHost;
        mTabHost.setup(getContext(), getChildFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("menu").setIndicator(buildTabLayout("")),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("main").setIndicator(buildTabLayout("Профком")),
                mpg_nf_fragment.class, null);
        View ttw = buildTabLayout("Профбюро");
        mTabHost.addTab(mTabHost.newTabSpec("pb").setIndicator(ttw),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("clubs").setIndicator(buildTabLayout("Клубы")),
                mpg_nf_fragment.class, null);
        setCurrentTab(ptt);
        if (mTabHost.getCurrentTab() !=0) {
            LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
            ll.setBackgroundColor(Color.parseColor("#01579b"));
            prevsel = ll;
        }

        Button mb = (Button) mTabHost.findViewById(R.id.menu_button);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.menuToggle();
            }
        });
        LinearLayout ml = (LinearLayout) mTabHost.findViewById(R.id.menu);
        ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.menuToggle();
            }
        });
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                is_ref = false;
                feedloader();
                if (prevsel != null) {
                    prevsel.setBackgroundColor(Color.parseColor("#1e88e5"));
                }
                if ((ma.tabstack.size() != 0 && ma.tabstack.get(ma.tabstack.size() - 1) != ptt && ma.tabstack.get(ma.tabstack.size() - 1) != mTabHost.getCurrentTab()) || (ma.tabstack.size() == 0)) {
                    Log.d("dg", String.valueOf(ptt));
                    ma.tabstack.add(ptt);
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("tab", mTabHost.getCurrentTab());
                editor.apply();
                LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
                ll.setBackgroundColor(Color.parseColor("#01579b"));
                prevsel = ll;
                ptt = mTabHost.getCurrentTab();
            }
        });

        final Bundle  bundle = getArguments();
        if (bundle != null) {
            setCurrentTab(bundle.getInt("position"));
        }

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
                            is_ref = false;
                            feedloader();
                            is_ref = false;
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (authorization.isOnline(getActivity())) {
                    is_ref = true;
                    feedloader();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return view;
    }

    private View buildTabLayout(String tag) {
        int img_src;
        View tab;
        if (tag.equals("")) {
            tab = getActivity().getLayoutInflater().inflate(R.layout.menutab, null);
        } else {
            tab = getActivity().getLayoutInflater().inflate(R.layout.new_tab_layout, null);
            TextView tw = (TextView) tab.findViewById(R.id.news_tab_caption);
            tw.setText(tag);
            switch (tag) {
                case "Клубы":
                    img_src = R.mipmap.ic_heart;
                    break;
                case "Профбюро":
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                    if (settings.getString("faculties_list", "").equals("62036525")) {
                        img_src = R.mipmap.ic_bio;
                    } else if (settings.getString("faculties_list", "").equals("38960668")) {
                        img_src = R.mipmap.ic_geog;
                    } else if (settings.getString("faculties_list", "").equals("52008207")) {
                        img_src = R.mipmap.ic_hist;
                    } else if (settings.getString("faculties_list", "").equals("101769965")) {
                        img_src = R.mipmap.ic_saf;
                    } else if (settings.getString("faculties_list", "").equals("12998578")) {
                        img_src = R.mipmap.ic_imei;
                    } else if (settings.getString("faculties_list", "").equals("49383548")) {
                        img_src = R.mipmap.ic_cp;
                    } else if (settings.getString("faculties_list", "").equals("58317625")) {
                        img_src = R.mipmap.ic_ffizh;
                    } else if (settings.getString("faculties_list", "").equals("49341230")) {
                        img_src = R.mipmap.ic_phys;
                    } else if (settings.getString("faculties_list", "").equals("62125802")) {
                        img_src = R.mipmap.ic_chem;
                    } else if (settings.getString("faculties_list", "").equals("44589291")) {
                        img_src = R.mipmap.ic_ui;
                    } else {
                        img_src = R.mipmap.ic_uf;
                    }
                    break;
                case "Профком":
                    img_src = R.mipmap.ic_launcher;
                    break;
                default:
                    return tab;
            }
            ImageView iv = (ImageView) tab.findViewById(R.id.news_tab_icon);
            iv.setImageResource(img_src);
        }
        return tab;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ma.tabstack.add(ptt);
        mTabHost = null;
    }

    private  List<String> getGroupsIDS() {
        List<String> IDS = new ArrayList<>();
        switch (tab) {
            case 1:
                IDS.add("-22122847");
                empty_feed_message = "";
                break;
            case 2:
                if (!settings.getString("faculties_list", "").equals("")) {
                    IDS.add("-" + settings.getString("faculties_list", ""));
                }
                empty_feed_message = "Факультет не выбран.\n Выберите ваш факультет в настройках.";
                break;
            case 3:
                if (settings.getBoolean("club_gos", false)) {
                    IDS.add("-65955842");
                }
                if (settings.getBoolean("club_sno", false)) {
                    IDS.add("-91188284");
                }
                if (settings.getBoolean("club_volonter", false)) {
                    IDS.add("-77276649");
                }
                if (settings.getBoolean("club_gost", false)) {
                    IDS.add("-61189805");
                }
                if (settings.getBoolean("club_kok", false)) {
                    IDS.add("-68602555");
                }
                empty_feed_message = "Ни одного клуба не выбрано.\n" +
                        "Выберите интересующие вас клубы в настройках.";
                break;
        }
        return IDS;
    }

    private void setCurrentTab(int ct) {
        mTabHost.setCurrentTab(ct);
        tab = mTabHost.getCurrentTab();
        is_ref = false;
        feedloader();
    }

    private void feedloader() {
        eelay.setVisibility(View.GONE);
        //is_ref = false;
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (offset == 0) {
            feedItems.clear();
            listAdapter.notifyDataSetChanged();
        }
        if (!is_ref) {
            swipeRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            24,
                            getResources().getDisplayMetrics()));
            swipeRefreshLayout.setRefreshing(true);
        }
        tab = mTabHost.getCurrentTab();
        List<String> GIDS = getGroupsIDS();
        Log.d("VKAT", settings.getString("VKAT", ""));
        if (settings.getString("VKAT", "").equals("")) {
            nff.tab = tab;
        } else {
            Log.d("VKAT", settings.getString("VKAT", ""));
            if (GIDS.size()>1) {
                ArrayList<VKRequest> req = new ArrayList<>();
                for (int i = 0; i< GIDS.size(); i++) {
                    VKRequest request = new VKRequest("execute.getfeed",
                            VKParameters.from(VKApiConst.OWNER_ID, GIDS.get(i),
                                    VKApiConst.OFFSET, offset,
                                    VKApiConst.ACCESS_TOKEN, settings.getString("VKAT", ""),
                                    "v", "3.0",
                                    VKApiConst.COUNT, String.valueOf(Math.round(100 / GIDS.size()))));
                    req.add(request);
                }
                VKBatchRequest batch = new VKBatchRequest(req.toArray(new VKRequest[req.size()]));
                batch.executeWithListener(new VKBatchRequest.VKBatchRequestListener() {
                    @Override
                    public void onComplete(VKResponse[] responses) {
                        super.onComplete(responses);
                        ArrayList<JSONObject> resp = new ArrayList<>();
                        for (VKResponse response : responses) {
                            resp.add(response.json);
                            Log.d("vkresp", response.responseString);
                        }
                        ParseJSONFeed(resp.toArray(new JSONObject[resp.size()]));
                    }
                    @Override
                    public void onError(VKError error) {
                        Log.d("VKERR", error.toString());
                    }
                });
            } else if (GIDS.size()>0) {
                VKRequest request = new VKRequest("execute.getfeed",
                        VKParameters.from(VKApiConst.OWNER_ID, GIDS.get(0),
                                VKApiConst.OFFSET, offset,
                                VKApiConst.ACCESS_TOKEN, settings.getString("VKAT", ""),
                                "v", "3.0"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        ParseJSONFeed(response.json);
                        Log.d("vkresp", response.responseString);
                    }

                    @Override
                    public void onError(VKError error) {
                        //Do error stuff
                        Log.d("VKERR", error.toString());
                    }

                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        //I don't really believe in progress
                        Log.d("VKAF", request.toString());
                    }
                });
            } else {
                TextView eet = (TextView) eelay.findViewById(R.id.eet);
                eet.setText(empty_feed_message);
                eelay.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void ParseJSONFeed(JSONObject... resultJson) {
        String urlgroupsimages = "";
        JSONObject dataJsonObj;
        if (resultJson.length>1) {
            Log.d("UP", "UP");
            List<JSONObject> jlist = new ArrayList<>();
            urlgroupsimages = "{\"groups\":{";
            for (JSONObject dataJson : resultJson) {
                try {
                    JSONObject groupJSON = dataJson.getJSONObject("response").getJSONObject("group");
                    urlgroupsimages += "\"" + groupJSON.getString("gid") + "\":{\"name\":\"" + groupJSON.getString("name") + "\",\"photo\":\"" + groupJSON.getString("photo_medium") + "\"},";
                    JSONArray feedArray = dataJson.getJSONObject("response").getJSONArray("all");
                    for (int j = 1; j < feedArray.length(); j++) {
                        Log.d("sort", feedArray.getJSONObject(j).getString("date"));
                        jlist.add(feedArray.getJSONObject(j));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            urlgroupsimages = urlgroupsimages.substring(0, urlgroupsimages.length() - 1)+"}}";
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
            try {
                resultJson = new JSONObject[1];
                resultJson[0] = new JSONObject("{\"response\":{\"all\":[2244," + ssum + "]}}");
            } catch (JSONException ignored) {}
        }
        if (resultJson.length == 1) {
            try {
                dataJsonObj = resultJson[0].getJSONObject("response");
                try {
                    if (is_ref || offset == 0) {
                        feedItems.clear();
                    }
                    JSONArray feedArray = dataJsonObj.getJSONArray("all");
                    JSONObject groups = null;
                    if (!urlgroupsimages.equals("")) {
                        groups = new JSONObject(urlgroupsimages).getJSONObject("groups");
                    } else {
                        //group info 1 group
                        JSONObject groupinfo = dataJsonObj.getJSONObject("group");
                        profimg = groupinfo.getString("photo_medium");
                        profname = groupinfo.getString("name");
                    }
                    for (int i = 1; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        final FeedItem item = new FeedItem();
                        item.setId(feedObj.getInt("id"));
                        if (groups != null) {
                            item.setName(groups.getJSONObject(feedObj.getString("to_id").substring(1)).getString("name"));
                        } else {
                            if (profname.length() != 0) {
                                item.setName(profname);
                            } else {
                                item.setName("Разное");
                            }
                        }
                        if (!feedObj.isNull("attachment")) {
                            /*if (feedObj.getJSONObject("attachment").has("video")) {
                                String JSON;
                                if (!settings.getString("VKAT", "").equals("") || !settings.getString("VKAT", "").equals("0")) {
                                    if (!isSDK) {
                                        try {
                                            URL url = new URL("https://api.vk.com/method/video.get?videos=" + feedObj.getJSONObject("attachment").getJSONObject("video").getString("owner_id") + "_" + feedObj.getJSONObject("attachment").getJSONObject("video").getString("vid") + "&count=1&extended=0&version=5.44&access_token=" + settings.getString("VKAT", ""));
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
                                }
                            }*/
                            JSONArray attachments = feedObj.getJSONArray("attachments");
                            JSONArray videos = dataJsonObj.getJSONArray("videos");
                            ArrayList<String> photo = new ArrayList<>();
                            for (int k = 0; k < attachments.length(); k++) {
                                if (attachments.getJSONObject(k).getString("type").equals("photo")) {
                                    JSONObject photojson = attachments.getJSONObject(k).getJSONObject("photo");
                                    if (k < 1) {
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
                                } else if (attachments.getJSONObject(k).getString("type").equals("link")) {
                                    item.setUrl(attachments.getJSONObject(k).getJSONObject("link").getString("url"));
                                } else if (attachments.getJSONObject(k).getString("type").equals("video")) {
                                    try {
                                        String vid = attachments.getJSONObject(k).getJSONObject("video").getString("vid");
                                        for(int l = 1; l< videos.length(); l++) {
                                            JSONObject video = videos.getJSONObject(l);
                                            if (video.getString("vid").equals(vid)) {
                                                if (video.has("player")) {
                                                    item.setExtvideo(video.getString("player"));
                                                    item.setVideoimg(video.getString("image_medium"));
                                                    //Log.d("video", video.getString("player"));
                                                } else if (video.has("files") && video.getJSONObject("files").has("mp4_360")) {
                                                    item.setExtvideo(video.getJSONObject("files").getString("mp4_360"));
                                                    item.setVideoimg(video.getString("image_medium"));
                                                    //Log.d("video", video.getJSONObject("files").getString("mp4_360"));
                                                } else if (video.has("files") && video.getJSONObject("files").has("mp4_240")) {
                                                    item.setExtvideo(video.getJSONObject("files").getString("mp4_240"));
                                                    item.setVideoimg(video.getString("image_medium"));
                                                   // Log.d("video", video.getJSONObject("files").getString("mp4_240"));
                                                }
                                                break;
                                            }
                                        }

                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                            item.setImage(photo);
                        }
                        item.setStatus(Html.fromHtml(feedObj.getString("text")).toString());
                        if (groups != null) {
                            item.setProfilePic(groups.getJSONObject(feedObj.getString("to_id").substring(1)).getString("photo").replace("\\", ""));
                        } else {
                            //profimg = feedArray.getJSONObject()
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
                    listAdapter.notifyDataSetChanged();
                    is_ref = false;
                    swipeRefreshLayout.setRefreshing(false);
                    offset = 0;
                    flag_loading = false;
                    ma.pt = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
}
