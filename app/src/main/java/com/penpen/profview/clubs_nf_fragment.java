package com.penpen.profview;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class clubs_nf_fragment extends nf_fragment {
      static {
            layid = R.id.clf;
            lay = R.layout.clubs_feed;
            lvid = R.id.listcl;
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
