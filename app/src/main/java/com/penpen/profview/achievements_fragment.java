package com.penpen.profview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapter.AchievementListAdapter;
import app.authorization;
import data.AchievementItem;

/**
 * Created by penpen on 02.11.15.
 */
public class achievements_fragment extends Fragment {

    private ListView listView;
    public List<AchievementItem> achievementItems;
    private AchievementListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout na;
    private MainActivity ma;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ma = (MainActivity) getActivity();
        if (!ma.isloginredirect) {
            new authcheck().execute();
        } else {
            ma.lf = null;
            ma.isloginredirect = false;
            FragmentManager fm = ma.getSupportFragmentManager();
            fm.popBackStack();
        }
        View view = inflater.inflate(R.layout.achievements_list_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.achievements_layout);
        na = (LinearLayout) view.findViewById(R.id.empty_ach);
        FloatingActionButton add = (FloatingActionButton) view.findViewById(R.id.addach);
        Button showmenu = (Button) view.findViewById(R.id.showmenu);

        showmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.menuToggle();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment af = new achievement_fragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, af)
                        .addToBackStack(null)
                        .commit();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (authorization.isOnline(getActivity())) {
                    achievementItems.clear();
                    new getAchievementsList().execute();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Проверьте интернет соединение", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        achievementItems = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.AchList);
        listAdapter = new AchievementListAdapter(getActivity(), achievementItems);
        if (authorization.isOnline(getActivity())) {
            new getAchievementsList().execute();
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Проверьте интернет соединение", Toast.LENGTH_SHORT);
            toast.show();
        }
        return view;
    }

    class getAchievementsList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                swipeRefreshLayout.setProgressViewOffset(false, 0,
                        (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                24,
                                getResources().getDisplayMetrics()));
                swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            Boolean result = false;
            /*if (authorization.cookie.length() != 0) {
                result = true;
            } else {*/
                String response;
                response = authorization.auth(getContext());
                try {
                    if ((!response.equals("error")) && (!response.equals("no_login")) && (response.length() != 0)) {
                        result = true;
                    } else if (response.equals("no_login")) {
                        result = false;
                    }
                } catch (Exception e) {
                    result = true;
                }
            //}
            if (result) {
                String HTML;
                try {
                    URL url = new URL("http://irk.yourplus.ru/rating/achievements/self/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(3000);
                    urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.addRequestProperty("Connection", "keep-alive");
                    urlConnection.addRequestProperty("Cookie", authorization.cookie);
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    HTML = buffer.toString();
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
                return HTML;
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() == 0) {
                try {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new login_fragment())
                            .commit();
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (NullPointerException ignored) { }
                swipeRefreshLayout.setRefreshing(false);
            } else {
                Document doc;
                doc = Jsoup.parse(result);
                try {
                Elements ratingTable = doc.getElementById("j-rating-table").getElementsByTag("tbody").first().getElementsByTag("tr");
                for (int i = 0; i < ratingTable.size(); i++) {
                    int isprem;
                    String pp = ratingTable.get(i).getElementsByTag("td").get(4).child(0).text();
                    switch (pp) {
                        case "Принято":
                            isprem = 0;
                            break;
                        case "Отклонено":
                            isprem = 2;
                            break;
                        case "На рассмотрении":
                            isprem = 1;
                            break;
                        default:
                            isprem = 2;
                            break;
                    }

                    int isstip;
                    String sp = ratingTable.get(i).getElementsByTag("td").get(5).child(0).text();
                    switch (sp) {
                        case "Принято":
                            isstip = 0;
                            break;
                        case "Отклонено":
                            isstip = 2;
                            break;
                        case "На рассмотрении":
                            isstip = 1;
                            break;
                        default:
                            isstip = 2;
                            break;
                    }

                    AchievementItem na = new AchievementItem();
                    na.setAchid(ratingTable.get(i).getElementsByTag("td").get(1).child(0).attr("href").replace("/rating/achievements/edit/", "").replace("/", ""));
                    na.setId(i);
                    na.setName(ratingTable.get(i).getElementsByTag("td").get(1).child(0).text());
                    na.setCategory(ratingTable.get(i).getElementsByTag("td").get(3).text());
                    na.setDate(ratingTable.get(i).getElementsByTag("td").get(2).text());
                    na.setDateofadd(ratingTable.get(i).getElementsByTag("td").get(0).text());
                    na.setIsprem(isprem);
                    na.setIsstip(isstip);
                    achievementItems.add(na);
                }
                listView.setAdapter(listAdapter);
                } catch (NullPointerException e) {
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                    na.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    class authcheck extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            //Log.d("cookie", authorization.cookie);
            /*if (authorization.cookie.length() != 0) {
                result = true;
            } else {*/
            String response = authorization.auth(getActivity());
            //Log.d("resp", response);
            try {
                if ((!response.equals("error")) && (!response.equals("no_login")) && (response.length() != 0)) {
                    result = true;
                } else if (response.equals("no_login")) {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
            //}
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result && authorization.cookie.length() == 0) {
                try {
                    ma.isloginredirect = true;
                    FragmentManager fragmentManager = getFragmentManager();
                    ma.lf = new login_fragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ma.lf)
                            .addToBackStack(null)
                            .commit();

                   // ma.changeFragment(0);
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception ignored) {

                }
            }
        }
    }
}
