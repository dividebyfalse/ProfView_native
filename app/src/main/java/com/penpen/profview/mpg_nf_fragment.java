package com.penpen.profview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class mpg_nf_fragment extends nf_fragment {
    @Override
    protected int getlvid() {
        return R.id.listmpg;
    }

    @Override
    protected int getlayid() {
        return R.id.mpg;
    }

    @Override
    protected String getee() {
        return "";
    }

    @Override
    protected int getlay() {
        return R.layout.mpg_feed;
    }

    @Override
    protected List<String> getURL() {
        List<String> urls = new ArrayList<>();
        urls.add("https://api.vk.com/method/wall.get?owner_id=-22122847&filter=all&count=100");
        return urls;
    }
}
