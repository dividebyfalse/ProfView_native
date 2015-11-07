package com.penpen.profview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
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

import app.authorization;

/**
 * Created by penpen on 02.11.15.
 */
public class login_fragment extends Fragment {
    public String phpsessid = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, container, false);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final EditText login = (EditText) view.findViewById(R.id.login);
        final EditText pass = (EditText) view.findViewById(R.id.pass);
        login.setText(settings.getString("login_preference", ""));
        pass.setText(settings.getString("pass_preference", ""));
        Button loginb = (Button) view.findViewById(R.id.loginbutton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new auth().execute(login.getText().toString(), pass.getText().toString());
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

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                return false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    new auth().execute(login.getText().toString(), pass.getText().toString());
                    return true;
                }
                return false;
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

    class auth extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String response = "";
            Boolean result = false;
            if (params.length>0) {
                if (params[0].length() != 0 && params[1].length() != 0) {
                    response = authorization.auth(getContext(), params[0], params[1]);
                    Log.d("df", response);
                }
            }
            if ((response.equals("error") == false) && (response.equals("no_login") == false) && (response.length() != 0)) {
                result = true;
            } else if (response.equals("no_login") == true) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == false) {
                /*//Todo: test this
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new login_fragment())
                        .commit();*/
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                EditText login = (EditText) getView().findViewById(R.id.login);
                EditText pass = (EditText) getView().findViewById(R.id.pass);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("login_preference", login.getText().toString());
                editor.putString("pass_preference", pass.getText().toString());
                editor.commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
