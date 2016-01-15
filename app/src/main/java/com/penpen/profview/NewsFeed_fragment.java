package com.penpen.profview;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 09.10.15.
 */
public class NewsFeed_fragment extends Fragment {
    public FragmentTabHost mTabHost;
    private LinearLayout prevsel;
    private int ptt;
    private MainActivity ma;
    private boolean createdTab = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.news_feed, container, false);
        ma = (MainActivity) getActivity();
        if (ma.tabstack.size() == 0) {
            ptt = 1;
            Log.d("dsf",String.valueOf(ptt));
        } else {
            ptt = ma.tabstack.get(ma.tabstack.size()-1);
            ma.tabstack.remove(ma.tabstack.size()-1);
            Log.d("ds",String.valueOf(ptt));
        }
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("menu").setIndicator(buildTabLayout("")),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("main").setIndicator(buildTabLayout("Профком")),
                mpg_nf_fragment.class, null);
        View ttw = buildTabLayout("Профбюро");
        mTabHost.addTab(mTabHost.newTabSpec("pb").setIndicator(ttw),
                pbf_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("clubs").setIndicator(buildTabLayout("Клубы")),
                clubs_nf_fragment.class, null);
        mTabHost.setCurrentTab(ptt);
        if (mTabHost.getCurrentTab() !=0) {
            LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
            ll.setBackgroundColor(Color.parseColor("#01579b"));
            prevsel = ll;
        }
        Button mb = (Button) mTabHost.findViewById(R.id.menu_button);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.menuToggle();
            }
        });
        LinearLayout ml = (LinearLayout) mTabHost.findViewById(R.id.menu);
        ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.menuToggle();
            }
        });

        LinearLayout ntl = (LinearLayout) ttw.findViewById(R.id.ntl);
        ntl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ma.pt != null) {
                     ma.pt.cancel(true);
                    ((FrameLayout) getView().findViewById(R.id.realtabcontent)).removeAllViews();
                }
                mTabHost.setCurrentTab(2);
            }
        });
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //((FrameLayout) getView().findViewById(R.id.realtabcontent)).removeAllViews();
                /*if (ma.pt != null) {
                   // ma.pt.cancel(true);
                    ((FrameLayout) getView().findViewById(R.id.realtabcontent)).removeAllViews();
                }*/
                if (prevsel != null) {
                    prevsel.setBackgroundColor(Color.parseColor("#1e88e5"));
                }
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if ((ma.tabstack.size() != 0 && ma.tabstack.get(ma.tabstack.size()-1) != ptt && ma.tabstack.get(ma.tabstack.size()-1) != mTabHost.getCurrentTab()) || (ma.tabstack.size() == 0)) {
                    Log.d("dg", String.valueOf(ptt));
                    ma.tabstack.add(ptt);
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("tab", mTabHost.getCurrentTab());
                editor.apply();
                LinearLayout ll = (LinearLayout) mTabHost.getCurrentTabView().findViewById(R.id.sel);
                ll.setBackgroundColor(Color.parseColor("#01579b"));
                prevsel = ll;
                ptt = mTabHost.getCurrentTab();
            }
        });

        final Bundle  bundle = getArguments();
        if (bundle != null) {
            mTabHost.setCurrentTab(bundle.getInt("position"));
        }
        return view;
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
        ma.tabstack.add(ptt);
        mTabHost = null;
    }
}
