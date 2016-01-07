package com.penpen.profview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import app.RegistrationIntentService;
import app.authorization;


/**
 * Created by penpen on 13.10.15.
 */
public class settings_fragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preference_fragment, s);
    }

    @Override public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        MainActivity ma = (MainActivity) getActivity();
        FragmentTransaction ft = ma.getSupportFragmentManager().beginTransaction();
        settings_fragment fragment = new settings_fragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.replace(R.id.container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(null);
        ft.commit();
        ma.menustack.add(4);
    }

    @Override
    public void onResume() {
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
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
