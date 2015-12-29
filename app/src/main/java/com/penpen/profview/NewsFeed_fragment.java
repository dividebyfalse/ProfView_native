package com.penpen.profview;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by penpen on 09.10.15.
 */
public class NewsFeed_fragment extends Fragment {
    private FragmentTabHost mTabHost;
    private LinearLayout prevsel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("menu").setIndicator(buildTabLayout("")),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("main").setIndicator(buildTabLayout("Профком")),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("pb").setIndicator(buildTabLayout("Профбюро")),
                pbf_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("clubs").setIndicator(buildTabLayout("Клубы")),
                clubs_nf_fragment.class, null);
        mTabHost.setCurrentTab(1);
        if (mTabHost.getCurrentTab() !=0) {
            LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
            ll.setBackgroundColor(Color.parseColor("#01579b"));
            prevsel = ll;
        }
        LinearLayout ml = (LinearLayout) mTabHost.findViewById(R.id.menu);
        ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                ma.menuToggle();
            }
        });
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (prevsel != null) {
                    prevsel.setBackgroundColor(Color.parseColor("#1e88e5"));
                }
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("tab", mTabHost.getCurrentTab());
                editor.apply();
                LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
                ll.setBackgroundColor(Color.parseColor("#01579b"));
                prevsel = ll;
            }
        });

        final Bundle  bundle = getArguments();
        if (bundle != null) {
            mTabHost.setCurrentTab(bundle.getInt("position"));
        }
        return mTabHost;
    }

    private View buildTabLayout(String tag) {
        int img_src;
        View tab;
        if (tag.equals("")) {
            tab = getActivity().getLayoutInflater().inflate(R.layout.menutab, null);
        } else {
            tab = getActivity().getLayoutInflater().inflate(R.layout.new_tab_layout, null);
            TextView tw = (TextView) tab.findViewById(R.id.news_tab_caption);
            tw.setText(tag);
            switch (tag) {
                case "Клубы":
                    img_src = R.mipmap.ic_heart;
                    break;
                case "Профбюро":
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                    if (settings.getString("faculties_list", "").equals("62036525")) {
                        img_src = R.mipmap.ic_bio;
                    } else if (settings.getString("faculties_list", "").equals("38960668")) {
                        img_src = R.mipmap.ic_geog;
                    } else if (settings.getString("faculties_list", "").equals("52008207")) {
                        img_src = R.mipmap.ic_hist;
                    } else if (settings.getString("faculties_list", "").equals("101769965")) {
                        img_src = R.mipmap.ic_saf;
                    } else if (settings.getString("faculties_list", "").equals("12998578")) {
                        img_src = R.mipmap.ic_imei;
                    } else if (settings.getString("faculties_list", "").equals("49383548")) {
                        img_src = R.mipmap.ic_cp;
                    } else if (settings.getString("faculties_list", "").equals("58317625")) {
                        img_src = R.mipmap.ic_ffizh;
                    } else if (settings.getString("faculties_list", "").equals("49341230")) {
                        img_src = R.mipmap.ic_phys;
                    } else if (settings.getString("faculties_list", "").equals("62125802")) {
                        img_src = R.mipmap.ic_chem;
                    } else if (settings.getString("faculties_list", "").equals("44589291")) {
                        img_src = R.mipmap.ic_ui;
                    } else {
                        img_src = R.mipmap.ic_uf;
                    }
                    break;
                case "Профком":
                    img_src = R.mipmap.ic_launcher;
                    break;
                default:
                    return tab;
            }
            ImageView iv = (ImageView) tab.findViewById(R.id.news_tab_icon);
            iv.setImageResource(img_src);
        }
        return tab;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
