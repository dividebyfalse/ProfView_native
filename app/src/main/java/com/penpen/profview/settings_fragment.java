package com.penpen.profview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceManager;

import app.RegistrationIntentService;
import app.authorization;


/**
 * Created by penpen on 13.10.15.
 */
public class settings_fragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_fragment);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (authorization.isOnline(getActivity())) {
            if (sharedPreferences.getBoolean("IsPushEnabled", true)) {
                Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
                getActivity().startService(intent);
            }
        } else {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sendprefsoninterneton", true);
            editor.apply();
        }
    }
}
