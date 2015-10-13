package com.penpen.profview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

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

        mTabHost.addTab(mTabHost.newTabSpec("Главная").setIndicator("Главная"),
                mpg_nf_fragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Профбюро факультета").setIndicator("Профбюро факультета"),
                pbf_nf_fragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Клубы").setIndicator("Клубы"),
                clubs_nf_fragment.class, null);
        return mTabHost;
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
