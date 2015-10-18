package app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.penpen.profview.MainActivity;
import com.penpen.profview.R;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by penpen on 18.10.15.
 */
public class NotificationService extends Service {

    private int lastId = 0;
    private HashMap<Integer, Notification> notifications;
    private NotificationManager manager;
    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        MyTask task = new MyTask();
        task.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    public int createInfoNotification(String message){
        Resources res = getBaseContext().getResources();
        manager = (NotificationManager) getBaseContext().getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
            Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class); // по клику на уведомлении откроется HomeActivity
            NotificationCompat.Builder nb = new NotificationCompat.Builder(getBaseContext())
                    .setSmallIcon(R.drawable.ic_profisu_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_profisu_notification))
                    .setAutoCancel(true)
                    .setTicker(message)
                    .setContentText(message)
                    .setContentIntent(PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("ProfISU")
                    .setDefaults(Notification.DEFAULT_ALL);
            Notification notification = nb.build();
            manager.notify(lastId, notification);
            notifications.put(lastId, notification);
            return lastId++;
    }


    class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... noargs) {
            createInfoNotification("test notif");
            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (int i = 1; i<=5; i++) {
                        Log.d(LOG_TAG, "i = " + i);
                    };
                };
            }, 0L, 2L * 1000);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}