package com.penpen.profview;

<<<<<<< HEAD
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FeedListAdapter;
import data.FeedItem;
=======
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
>>>>>>> c807a6c82e85d3ba5d59fb3c77012286c21035a3

/**
 * Created by penpen on 02.11.15.
 */
public class achievements_fragment extends Fragment {

<<<<<<< HEAD
    private ListView listView;
    public ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_list_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.AchList);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listItems);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final StringRequest achreq = new StringRequest(Request.Method.GET, "http://irk.yourplus.ru/rating/achievements/self/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document doc = null;
                        doc = Jsoup.parse(response);
                        Elements ratingTable = doc.getElementById("j-rating-table").getElementsByTag("tbody").first().getElementsByTag("tr");
                        for (int i=0; i<ratingTable.size(); i++) {
                            listItems.add(ratingTable.get(i).getElementsByTag("td").get(1).child(0).text());
                            Log.d("b", ratingTable.get(i).getElementsByTag("td").get(1).child(0).text());
                            Log.d("b", "separator");
                        }
                        listView.setAdapter(listAdapter);
                        //phpsessid = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Connection", "keep-alive");
                params.put("Cookie", settings.getString("cookie", ""));
                return params;
            }
           /* @Override
            protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
                String sessionId = networkResponse.headers.get("Set-Cookie");
                com.android.volley.Response<String> result = com.android.volley.Response.success(sessionId,
                        HttpHeaderParser.parseCacheHeaders(networkResponse));
                return result;
            }*/

        };
        queue.add(achreq);
=======
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievements_list_fragment, container, false);

>>>>>>> c807a6c82e85d3ba5d59fb3c77012286c21035a3

        return view;
    }

<<<<<<< HEAD



=======
>>>>>>> c807a6c82e85d3ba5d59fb3c77012286c21035a3
}
