package com.penpen.profview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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

import app.authorization;

/**
 * Created by penpen on 02.11.15.
 */
public class achievements_fragment extends Fragment {

    private ListView listView;
    public ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter listAdapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_list_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.AchList);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listItems);
        progressDialog = ProgressDialog.show(view.getContext(), "", "Загрузка. Подождите...", true);
        new getAchievementsList().execute();
        return view;
    }


    class getAchievementsList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Boolean result = false;
            if (authorization.cookie.length() != 0) {
                result = true;
            } else {
                String response = "";
                response = authorization.auth(getContext());
                Log.d("df", response);
                if ((response.equals("error") == false) && (response.equals("no_login") == false) && (response.length() != 0)) {
                    result = true;
                } else if (response.equals("no_login") == true) {
                    result = false;
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
                }catch(Exception e){
                    e.printStackTrace();
                }
                return HTML;
            } else {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() == 0) {
                //Todo: test this
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new login_fragment())
                        .commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Document doc = null;
                doc = Jsoup.parse(result);
                Elements ratingTable = doc.getElementById("j-rating-table").getElementsByTag("tbody").first().getElementsByTag("tr");
                for (int i=0; i<ratingTable.size(); i++) {
                    listItems.add(ratingTable.get(i).getElementsByTag("td").get(1).child(0).text());
                    Log.d("b", ratingTable.get(i).getElementsByTag("td").get(1).child(0).text());
                    Log.d("b", "separator");
                }
                listView.setAdapter(listAdapter);
                progressDialog.dismiss();
            }
        }
    }

}
