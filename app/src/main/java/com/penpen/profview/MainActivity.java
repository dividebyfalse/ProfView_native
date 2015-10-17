package com.penpen.profview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity {
    SlidingMenu menu;
    private PreferenceFragment SettingsFragment;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.sidemenu);
        menu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);

        String[] items = {"Войти", "Новости","Подать достижение", "Список достижений", "Настройки"};
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
            }
        });
        setContentView(R.layout.activity_main);
        changeFragment(1);
    }

    public void menuToggle(){
        if(menu.isMenuShowing())
            menu.showContent();
        else
            menu.showMenu();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(menu.isMenuShowing()){
                menu.toggle(true);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeFragment(int position) {
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.container);
        switch (position) {
            case 0:
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
                showFragment(new achievement_fragment());
                break;
            case 3:
                break;
            case 4:
                mainLayout.setBackgroundColor(Color.BLACK);
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
        super.onPause();
        SharedPreferences settings = getSharedPreferences("temp", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("my", "");
        editor.commit();
    }
}
