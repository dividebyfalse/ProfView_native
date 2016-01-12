package com.penpen.profview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.List;

import adapter.MenuListAdapter;
import app.QuickstartPreferences;
import app.RegistrationIntentService;
import data.MenuItem;

public class MainActivity extends FragmentActivity {
    public SlidingMenu menu;
    private Fragment lf=null;
    private Fragment nff = null;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    static public boolean isLogin;
    static public List<MenuItem> items;
    static public MenuListAdapter menuadapter = null;
    static public int fragmentnumber;
    public List<Integer> tabstack;
    public List<Integer> menustack;
    public int menuselected;
    public boolean isImageFitToScreen;
    public AsyncTask pt;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isImageFitToScreen = false;

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

        tabstack = new ArrayList<>();
        menustack = new ArrayList<>();

        items = new ArrayList<>();
        items.add(new MenuItem("Войти"));
        items.add(new MenuItem("Новости"));
        items.add(new MenuItem("Сообщения"));
        items.add(new MenuItem("Список достижений"));
        items.add(new MenuItem("Настройки"));
        String log = "Войти";
        isLogin = false;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getString("login_preference", "").length() !=0) {
            log = "Выйти";
            isLogin = true;
        }
        if (settings.getString("VKAT", "").equals("")) {
            VKSdk.login(this, "offline", "audio", "video");
        }
        menuadapter = new MenuListAdapter(this, items);
        items.get(0).setDescription(log);
        ListView listView = ((ListView) findViewById(R.id.sidemenu));
        listView.setAdapter(menuadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuToggle();
                changeFragment(position);
            }
        });
        setContentView(R.layout.activity_main);

        if (!settings.contains("IsPushEnabled")) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("IsPushEnabled", true);
            editor.apply();
        }

        Intent initintent = getIntent();
        if (initintent.getBooleanExtra("isMessageList", false)) {
            changeFragment(2);
        } else if ((initintent.getStringExtra("newsid") != null) && !initintent.getStringExtra("newsid").equals("")) {
            showextendednews(initintent);
        } else {
            changeFragment(1);
        }

        if (settings.getBoolean("IsPushEnabled", true)) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("VKAT", res.accessToken);
                editor.apply();
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("VKAT", "0");
                editor.apply();
                Log.d("err", "vkacceserror");
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
            LinearLayout loginlay = null;
            ScrollView reglay = null;
            if (lf !=null) {
                loginlay = (LinearLayout) lf.getView().findViewById(R.id.loginlayout);
                reglay = (ScrollView) lf.getView().findViewById(R.id.scrollreglayout);
            }

            if(menu.isMenuShowing()){
                menu.toggle();
            } else if (lf != null && lf.isVisible() && loginlay.getVisibility() == View.INVISIBLE) {
                loginlay.setVisibility(View.VISIBLE);
                reglay.setVisibility(View.INVISIBLE);
            } else if (nff != null && nff.isVisible()) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("fragmentnumber", "");
                editor.apply();
                NewsFeed_fragment myFragment = (NewsFeed_fragment) nff;
                if (nff != null && nff.isVisible()) {
                    if (tabstack.size() != 0) {
                        myFragment.mTabHost.setCurrentTab(tabstack.get(tabstack.size() - 1));
                        tabstack.remove(tabstack.size() - 1);
                        return true;
                    } else {
                        return super.onKeyDown(keyCode, event);
                    }
                }
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("fragmentnumber", "");
                editor.apply();
                return super.onKeyDown(keyCode, event);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu.toggle();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            if (fm.getBackStackEntryCount() == 1) {
                fm.popBackStack();
                changeFragment(1);
            } else {
                fm.popBackStack();
                Log.d("pbstck", String.valueOf(menustack.get(menustack.size() - 1)));
                items.get(menustack.get(menustack.size()-1)).setSelected(true);
                menuselected=menustack.get(menustack.size()-1);
                menustack.remove(menustack.get(menustack.size()-1));
                menuadapter.notifyDataSetChanged();
            }
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    public void showextendednews(Intent i) {
        menustack.add(menuselected);
        Bundle args = new Bundle();
        args.putString("newsid", i.getStringExtra("newsid"));
        nff = new news_item_fragment();
        nff.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nff)
                .addToBackStack(null)
                .commit();
    }

    public void changeFragment(int position) {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.container);
        lf=null;
        nff = null;
        menustack.add(menuselected);
        menuselected = position;
        items.get(position).setSelected(true);
        menuadapter.notifyDataSetChanged();
        mainLayout.setBackgroundColor(Color.parseColor("#d3d6db"));
        fragmentnumber = position;
        switch (position) {
            case 0:
                if (isLogin) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("login_preference", "");
                    editor.putString("pass_preference", "");
                    editor.apply();
                    items.get(0).setDescription("Войти");
                    menuadapter.notifyDataSetChanged();
                    isLogin = false;
                }
                lf = new login_fragment();
                showFragment(lf);
                break;
            case 1:
                nff = new NewsFeed_fragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, nff)
                        .commit();
                //showFragment(nf);
                break;
            case 2:
                showFragment(new push_message_list_fragment());
                break;
            case 3:
                showFragment(new achievements_fragment());
                break;
            case 4:
                showFragment(new settings_fragment());
                break;
        }
    }

   private void showFragment(Fragment currentFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, currentFragment)
                .addToBackStack(null)
                .commit();
   }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("fragmentnumber", 1);
        editor.apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("fragmentnumber", 1);
        editor.apply();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("fragmentnumber", fragmentnumber);
        //editor.putString("cookie", authorization.cookie);
        editor.apply();
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
