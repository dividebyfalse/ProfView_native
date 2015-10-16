package com.penpen.profview;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import 	android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v4.app.Fragment;


/**
 * Created by penpen on 13.10.15.
 */
public class settings_fragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_fragment);
    }

}
