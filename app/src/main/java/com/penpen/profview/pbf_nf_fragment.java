package com.penpen.profview;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class pbf_nf_fragment extends nf_fragment {
    @Override
    protected int getlvid() {
        return R.id.listpbf;
    }

    @Override
    protected int getlayid() {
        return R.id.pbf;
    }


    @Override
    protected String getee() {
        return "Факультет не выбран.\n" +
                "Выберите ваш факультет в настройках.";
    }

    @Override
    protected int getlay() {
        return R.layout.pbf_feed;
    }

    @Override
    protected List<String> getURL() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> urls = new ArrayList<>();
        if (!settings.getString("faculties_list", "").equals("")) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-" + settings.getString("faculties_list", "") + "&filter=all&count=100");
        }
        return urls;
    }
}
