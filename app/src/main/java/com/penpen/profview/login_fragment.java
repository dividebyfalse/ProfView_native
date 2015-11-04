package com.penpen.profview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by penpen on 02.11.15.
 */
public class login_fragment extends Fragment {
    public String phpsessid = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, container, false);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final EditText login = (EditText) view.findViewById(R.id.login);
        final EditText pass = (EditText) view.findViewById(R.id.pass);
        login.setText(settings.getString("login_preference", ""));
        pass.setText(settings.getString("pass_preference", ""));
        final StringRequest sessidreq = new StringRequest(Request.Method.GET, "http://irk.yourplus.ru/personal/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        phpsessid = response;
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
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
                String sessionId = networkResponse.headers.get("Set-Cookie");
                com.android.volley.Response<String> result = com.android.volley.Response.success(sessionId,
                        HttpHeaderParser.parseCacheHeaders(networkResponse));
                return result;
            }

        };
        queue.add(sessidreq);
        Button loginb = (Button) view.findViewById(R.id.loginbutton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phpsessid.length() != 0) {
                    try {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://irk.yourplus.ru/personal/?login=yes",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String bitrixlogin = response.substring(0, response.indexOf(";"));
                                            SharedPreferences.Editor editor = settings.edit();
                                            if (login.getText().toString().equals(bitrixlogin.substring(16))) {
                                                Log.d("succ", bitrixlogin.substring(16));
                                                editor.putString("login_preference", login.getText().toString());
                                                editor.putString("pass_preference", pass.getText().toString());
                                                editor.putString("cookie", bitrixlogin+"; BITRIX_SM_SOUND_LOGIN_PLAYED=Y; " + phpsessid.substring(0, phpsessid.indexOf(";")));
                                                editor.commit();
                                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT);
                                                toast.show();
                                            } else {
                                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        } catch(Exception e) {
                                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }){
                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("backurl", "/personal/");
                                params.put("AUTH_FORM", "Y");
                                params.put("TYPE", "AUTH");
                                params.put("USER_LOGIN", login.getText().toString());
                                params.put("USER_PASSWORD", pass.getText().toString());
                                params.put("logout_butt", "Войти");
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("Content-Type","application/x-www-form-urlencoded");
                                params.put("Connection", "keep-alive");
                                params.put("Cookie", phpsessid.substring(0, phpsessid.indexOf(";")));
                                return params;
                            }
                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
                                String sessionId = networkResponse.headers.get("Set-Cookie");
                                com.android.volley.Response<String> result = com.android.volley.Response.success(sessionId,
                                        HttpHeaderParser.parseCacheHeaders(networkResponse));
                                return result;
                            }
                        };
                        queue.add(stringRequest);

                    } catch (Exception e) {
                    }
                }
            }
        });

        Button regb = (Button) view.findViewById(R.id.regbutton);
        regb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
                loginlay.setVisibility(View.INVISIBLE);
                if (login.getText().length() != 0) {
                    EditText reglogin = (EditText) view.findViewById(R.id.reglogin);
                    reglogin.setText(login.getText().toString());
                }
                if (pass.getText().length() != 0) {
                    EditText regpass = (EditText) view.findViewById(R.id.regpass);
                    regpass.setText(pass.getText().toString());
                }
                AccountManager manager = (AccountManager) getActivity().getSystemService(getActivity().ACCOUNT_SERVICE);
                Account[] list = manager.getAccounts();
                String gmail = "";
                for(Account account: list)
                {
                    if(account.type.equalsIgnoreCase("com.google"))
                    {
                        gmail = account.name;
                        break;
                    }
                }
                EditText regmail = (EditText) view.findViewById(R.id.regemail);
                regmail.setText(gmail);
                new getCAPTCHA().execute();
                ScrollView reglay = (ScrollView) view.findViewById(R.id.scrollreglayout);
                reglay.setVisibility(View.VISIBLE);
            }
        });

        Button sendregb = (Button) view.findViewById(R.id.regbuttonsend);
        sendregb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
                loginlay.setVisibility(View.VISIBLE);
                ScrollView reglay = (ScrollView) view.findViewById(R.id.scrollreglayout);
                reglay.setVisibility(View.INVISIBLE);
            }
        });

        Button refreshcaptcha = (Button) view.findViewById(R.id.refreshcaptcha);
        refreshcaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getCAPTCHA().execute();
            }
        });

        return view;
    }

    class getCAPTCHA extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Document doc = null;
            URL url = null;
            try {
                url = new URL("http://irk.yourplus.ru/registration/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                doc = Jsoup.parse(url, 6000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap CAPTCHA = null;
            if (doc!=null) {
                Elements images =doc.body().getElementsByTag("img");
                String CAPTCHAstr = images.attr("alt", "CAPTCHA").get(images.attr("alt", "CAPTCHA").size() - 1).toString();
                Log.d("b", CAPTCHAstr);
                CAPTCHAstr = "http://irk.yourplus.ru" + CAPTCHAstr.substring(CAPTCHAstr.indexOf("src=")+5, CAPTCHAstr.indexOf("\"", CAPTCHAstr.indexOf("src=")+5));
                try {
                    InputStream in = new java.net.URL(CAPTCHAstr).openStream();
                    CAPTCHA = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return CAPTCHA;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                ImageView captchaIMG = (ImageView) getActivity().findViewById(R.id.regcaptcha);
                captchaIMG.setImageBitmap(result);
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Невозможно загрузить каптчу, попробуйте позже", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
