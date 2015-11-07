package app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by penpen on 06.11.15.
 */
public  class  authorization {
    static public String cookie = "";
    static private String retval;
    static private String login;
    static private String pass;

    static public String auth(Context context, String... val) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (val.length>1) {
            if ((val[0].length() !=0) && (val[1].length() !=0)) {
                login = val[0];
                pass = val[1];
            }
        } else if (settings.contains("login_preference") && settings.contains("pass_preference")) {
            login = settings.getString("login_preference", "");
            pass = settings.getString("pass_preference", "");
        } else {
            retval = "no_login";
            return retval;
        }
        if ((login.length() != 0) && (pass.length() != 0)) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://irk.yourplus.ru/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                final String cookie = urlConnection.getHeaderField("Set-Cookie");
                urlConnection.disconnect();
                url = new URL("http://irk.yourplus.ru/personal/?login=yes");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Cookie", cookie.substring(0, cookie.indexOf(";")));
                urlConnection.setDoInput(true);
                String params = "?backurl=/personal/&AUTH_FORM=Y&TYPE=AUTH&USER_LOGIN=" + login + "&USER_PASSWORD=" + pass + "&logout_butt=Войти";
                urlConnection.getOutputStream().write(params.getBytes("UTF-8"));
                urlConnection.connect();
                try {
                    final String out = urlConnection.getHeaderField("Set-Cookie");
                    urlConnection.disconnect();
                    if (out.equals("BITRIX_SM_SOUND_LOGIN_PLAYED=Y; path=/; domain=yourplus.ru")) {
                        Log.d("connected", "yes");
                        retval = "BITRIX_SM_LOGIN=isuStudent1594; BITRIX_SM_SOUND_LOGIN_PLAYED=Y; " + cookie.substring(0, cookie.indexOf(";"));
                    }
                } catch (Exception e) {
                    retval = "error";
                    Log.d("connected", "no");
                }
            } catch (Exception e) {
                retval = "error";
            }
            cookie = retval;
        }
        return retval;
    }
}
