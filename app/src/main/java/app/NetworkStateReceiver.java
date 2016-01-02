package app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.preference.PreferenceManager;
/**
 *
 * Created by penpen on 01.01.16.
 *
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                if (settings.getBoolean("sendprefsoninterneton", true)) {
                    Intent ointent = new Intent(context, RegistrationIntentService.class);
                    context.startService(ointent);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("sendprefsoninterneton", false);
                    editor.apply();
                }
            }
        }
    }
}
