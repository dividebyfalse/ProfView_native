package app;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.penpen.profview.MainActivity;
import com.penpen.profview.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by penpen on 20.10.15.
 */
public class MyGcmListenerService extends GcmListenerService {
    private DBHelper dbHelper;
    private static final String TAG = "MyGcmListenerService";
    private String message;
    private  MyGcmListenerService th;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        th = this;
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + data);
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (data.getString("toAll").equals("true")) {
            cv.put("message", data.getString("message"));
            cv.put("date", data.getString("date"));
            cv.put("isnew", 1);
            db.insert("pushmessagetable", null, cv);
            db.close();
            dbHelper.close();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            if (settings.getBoolean("IsPushEnabled", true)) {
                sendNotification(data.getString("message"), "PushMessageActivity");
            }
        } else if (data.getString("toAll").equals("false")) {
            message = data.getString("message");
            String [] items = data.getString("items").substring(1, data.getString("items").length()-1).split(",");
            for(int i=0;i<items.length; i++) {
                items[i]= items[i].replace("\"", "");
            }
            for(int i=0;i<items.length; i++) {
                if (i == items.length-1) {
                    new getFullItem().execute(items[i], "last");
                } else {
                    new getFullItem().execute(items[i]);
                }
            }
        }

        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     *
     *
     */
    private void sendNotification(String message, String activity) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isMessageList", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_profisu_notification)
                .setContentTitle("ProfView")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setColor(Color.RED);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("log", "--- onCreate database ---");
            db.execSQL("create table pushmessagetable ("
                    + "id integer primary key autoincrement,"
                    + "message text,"
                    + "date text,"
                    + "isnew int,"
                    + "isnews int,"
                    + "groupimage text,"
                    + "mainimage text,"
                    + "groupdesc text"+");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("log", "--- onUpdate database ---");
            db.execSQL("DROP TABLE IF EXISTS pushmessagetable");
            db.execSQL("create table pushmessagetable ("
                    + "id integer primary key autoincrement,"
                    + "message text,"
                    + "date text,"
                    + "isnew int,"
                    + "isnews int,"
                    + "groupimage text,"
                    + "mainimage text,"
                    + "groupdesc text"+");");
        }
    }

    class getFullItem extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {
            String JSON = "";
            try {
                URL url = new URL("https://api.vk.com/method/wall.getById?posts="+params[0]+"&extended=1&copy_history_depth=2&v=5.41");
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
            if (params.length>1 && params[1].equals("last")) {
                JSON=JSON+"1";
            }
            return JSON;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                String date = "";
                String text = "";
                String image = "";
                String profile_pic = "";
                String profile_name = "";
                Boolean islast = false;
                if (result.substring(result.length()-1, result.length()).equals("1")) {
                    result = result.substring(0, result.length()-1);

                    islast=true;
                }
                try {
                    JSONObject dataJsonObj = new JSONObject(result).getJSONObject("response");
                    JSONArray items = dataJsonObj.getJSONArray("items");
                    date = items.getJSONObject(0).getString("date");
                    text = items.getJSONObject(0).getString("text");
                    if (items.getJSONObject(0).has("attachments") && items.getJSONObject(0).getJSONArray("attachments").length()>0 && items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).has("photo")) {
                        if (items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_807")) {
                            image = items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_807");
                        } else if (items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_604")) {
                            image = items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_604");
                        }
                    }
                    profile_name = dataJsonObj.getJSONArray("groups").getJSONObject(0).getString("name");
                    profile_pic = dataJsonObj.getJSONArray("groups").getJSONObject(0).getString("photo_200");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dbHelper = new DBHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("message", text);
                cv.put("date", date);
                cv.put("isnew", 1);
                cv.put("isnews", 1);
                cv.put("groupimage", profile_pic);
                cv.put("mainimage", image);
                cv.put("groupdesc", profile_name);
                db.insert("pushmessagetable", null, cv);
                db.close();
                if (islast) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(th);
                    if (settings.getBoolean("IsPushEnabled", true)) {
                        sendNotification(message, "PushMessageActivity");
                    }
                }
            }
        }
    }
}