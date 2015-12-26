package com.penpen.profview;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class pbf_nf_fragment extends nf_fragment {
    static {
        layid = R.id.pbf;
        lay = R.layout.pbf_feed;
        lvid = R.id.listpbf;
        ee = "Факультет не выбран.\nВыберите ваш факультет в настройках.";
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
