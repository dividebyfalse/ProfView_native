package com.penpen.profview;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class clubs_nf_fragment extends nf_fragment {
    @Override
    protected int getlvid() {
        return R.id.listcl;
    }

    @Override
    protected int getlayid() {
        return R.id.clf;
    }


    @Override
    protected String getee() {
        return "Ни одного клуба не выбрано.\n" +
                "Выберите интересующие вас клубы в настройках.";
    }

    @Override
    protected int getlay() {
        return R.layout.clubs_feed;
    }

    @Override
    protected List<String> getURL() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> urls = new ArrayList<>();
        if (settings.getBoolean("club_gos", false)) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-65955842&filter=all");
        }
        if (settings.getBoolean("club_sno", false)) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-91188284&filter=all");
        }
        if (settings.getBoolean("club_volonter", false)) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-77276649&filter=all");
        }
        if (settings.getBoolean("club_gost", false)) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-61189805&filter=all");
        }
        if (settings.getBoolean("club_kok", false)) {
            urls.add("https://api.vk.com/method/wall.get?owner_id=-68602555&filter=all");
        }
        return urls;
    }
}
