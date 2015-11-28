package com.penpen.profview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ScrollView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import app.QuickstartPreferences;
import app.RegistrationIntentService;

public class MainActivity extends FragmentActivity {
    SlidingMenu menu;
    private PreferenceFragment SettingsFragment;
    private Fragment lf=null;
    private push_message_list_fragment mf = null;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private View prevmenusel;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = new SlidingMenu(this);
        menu.setBackgroundColor(0xFF333333);
        menu.setSelectorDrawable(R.drawable.sidemenu_items_background);
        menu.setSelectorEnabled(true);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sidemenu);
        menu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);


        String[] items = {"Войти", "Новости", "Сообщения", "Подать достижение", "Список достижений", "Настройки"};
        ((ListView) findViewById(R.id.sidemenu)).setAdapter(
                new ArrayAdapter<Object>(
                        this,
                        R.layout.sidemenu_item,
                        R.id.text,
                        items
                )
        );
        ((ListView) findViewById(R.id.sidemenu)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuToggle();
                changeFragment(position);
                if (prevmenusel != null) {
                    LinearLayout sel = (LinearLayout) prevmenusel.findViewById(R.id.sel);
                    sel.setVisibility(View.INVISIBLE);
                }
                LinearLayout sel = (LinearLayout) view.findViewById(R.id.sel);
                sel.setVisibility(View.VISIBLE);
                prevmenusel =view;
            }
        });
        setContentView(R.layout.activity_main);

        Intent initintent = getIntent();
        if (initintent.getBooleanExtra("isMessageList", false)) {
            changeFragment(2);
        } else if (initintent.getBooleanExtra("ShowMessageItem", false)) {
            newMessage(initintent.getStringExtra("message"), initintent.getStringExtra("date"), initintent.getIntExtra("position", -1));
        } else {
            changeFragment(1);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("IsPushEnabled", true)) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        //  mInformationTextView.setText(getString(R.string.gcm_send_message));
                    } else {
                        // mInformationTextView.setText(getString(R.string.token_error_message));
                    }
                }
            };

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> urls = new ArrayList<>();
        //Set<String> els = new ArraySet<>();
        Log.d("b", settings.getString("faculties_list", ""));
    }

    public void menuToggle(){
        if(menu.isMenuShowing())
            menu.showContent();
        else
            menu.showMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(menu.isMenuShowing()){
                menu.toggle();
            }
            LinearLayout loginlay = null;
            ScrollView reglay = null;
            LinearLayout messageitemlay = null;
            LinearLayout messagelistlay = null;
            if (lf !=null) {
                loginlay = (LinearLayout) lf.getView().findViewById(R.id.loginlayout);
                reglay = (ScrollView) lf.getView().findViewById(R.id.scrollreglayout);
            }
            if (mf != null) {
                messageitemlay = (LinearLayout) mf.getView().findViewById(R.id.messageread);
                messagelistlay = (LinearLayout) mf.getView().findViewById(R.id.messagelistlay);
            }
                if (lf != null && lf.isVisible() && loginlay.getVisibility() == View.INVISIBLE) {
                    loginlay.setVisibility(View.VISIBLE);
                    reglay.setVisibility(View.INVISIBLE);
                } else if (mf != null && mf.isVisible() && messageitemlay.getVisibility() == View.VISIBLE) {
                    messageitemlay.setVisibility(View.INVISIBLE);
                    messagelistlay.setVisibility(View.VISIBLE);
                    changeFragment(2);
                } else {
                    return super.onKeyDown(keyCode, event);
                }
                return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu.toggle();
                return true;
        }
        return true;
    }

    public void newMessage(String message, String date, int position) {
        mf = new push_message_list_fragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("date", date);
        args.putInt("position", position);
        mf.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mf)
                .commit();
    }

    private void changeFragment(int position) {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.container);
        mf = null;
        lf=null;
        switch (position) {
            case 0:
                if (SettingsFragment != null) {
                    getFragmentManager().beginTransaction().remove(SettingsFragment).commit();
                }
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                //showFragment(new login_fragment());
                lf = new login_fragment();
                showFragment(lf);
                break;
            case 1:
                if (SettingsFragment != null) {
                    getFragmentManager().beginTransaction().remove(SettingsFragment).commit();
                }
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                showFragment(new NewsFeed_fragment());
                break;
            case 2:
                if (SettingsFragment != null) {
                    getFragmentManager().beginTransaction().remove(SettingsFragment).commit();
                }
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                mf = new push_message_list_fragment();
                showFragment(mf);
                break;
            case 3:
                if (SettingsFragment != null) {
                    getFragmentManager().beginTransaction().remove(SettingsFragment).commit();
                }
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                showFragment(new achievement_fragment());
                break;
            case 4:
                if (SettingsFragment != null) {
                    getFragmentManager().beginTransaction().remove(SettingsFragment).commit();
                }
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                showFragment(new achievements_fragment());
                break;
            case 5:
                mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
                SettingsFragment = new settings_fragment();
                getFragmentManager().beginTransaction().replace(R.id.container, SettingsFragment).commit();
                showFragment(new settings_helper());
                break;
        }
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

   private void showFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment)
                .commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences settings = getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "test");
        editor.commit();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        SharedPreferences settings = getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "");
        editor.commit();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
