package com.penpen.profview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by penpen on 09.10.15.
 */
public class NewsFeed_fragment extends Fragment {
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabHost = new FragmentTabHost(getActivity());;
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("main").setIndicator(buildTabLayout("Профком ИГУ")),
                mpg_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("pb").setIndicator(buildTabLayout("Профбюро факультета")),
                pbf_nf_fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("clubs").setIndicator(buildTabLayout("Клубы")),
                clubs_nf_fragment.class, null);

        return mTabHost;
    }

    private View buildTabLayout(String tag) {
        int img_src;
        View tab = getActivity().getLayoutInflater().inflate(R.layout.new_tab_layout, null);
        ImageView iv = (ImageView) tab.findViewById(R.id.news_tab_icon);
        TextView tw = (TextView) tab.findViewById(R.id.news_tab_caption);
        tw.setText(tag);
        if (tag == "Клубы") {
            img_src = R.drawable.ic_clubs;
        } else if (tag == "Профбюро факультета") {
            img_src = R.drawable.ic_prof;
        } else if (tag == "Профком ИГУ") {
            img_src = R.drawable.ic_tab_home;
        } else {
            return tab;
        }
        iv.setImageResource(img_src);
        return tab;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
