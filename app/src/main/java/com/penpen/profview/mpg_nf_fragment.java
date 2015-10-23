package com.penpen.profview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class mpg_nf_fragment extends nf_fragment {
    static {
        layid = R.id.mpg;
        lay = R.layout.mpg_feed;
        lvid = R.id.listmpg;
    }

    @Override
    protected List<String> getURL() {
        List<String> urls = new ArrayList<>();
        urls.add("https://api.vk.com/method/wall.get?owner_id=-53393178&filter=all&count=100");
        return urls;
    }
}
