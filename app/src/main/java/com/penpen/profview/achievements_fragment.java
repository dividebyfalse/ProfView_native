package com.penpen.profview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_list_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.achievements_layout);
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
        achievementItems = new ArrayList<AchievementItem>();
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
                     if (authorization.cookie.length() != 0) {
                         result = true;
                     } else {
                         String response = "";
                         response = authorization.auth(getContext());
                         try {
                             if ((response.equals("error") == false) && (response.equals("no_login") == false) && (response.length() != 0)) {
                                 result = true;
                             } else if (response.equals("no_login") == true) {
                                 result = false;
                             }
                         } catch (Exception e) {
                             result = true;
                         }
                     }
                     if (result) {
                         String HTML = "";
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
                             StringBuffer buffer = new StringBuffer();
                             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                             String line;
                             while ((line = reader.readLine()) != null) {
                                 buffer.append(line);
                             }
                             HTML = buffer.toString();
                             urlConnection.disconnect();
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         return HTML;
                     } else {
                         return "";
                     }
                 }

                 @Override
                 protected void onPostExecute(String result) {
                     if (result.length() == 0) {
                         swipeRefreshLayout.setRefreshing(false);
                         FragmentManager fragmentManager = getFragmentManager();
                         fragmentManager.beginTransaction()
                                 .replace(R.id.container, new login_fragment())
                                 .commit();
                         Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                         toast.show();
                     } else {
                         Document doc = null;
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
                         //TODO: список достижений пуст listItems.add("Вы пока что не добавили каких либо достижений");
                         }
                         swipeRefreshLayout.setRefreshing(false);
                     }
                 }
             }

         }
