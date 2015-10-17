package com.penpen.profview;

/**
 * Created by penpen on 13.10.15.
 */
public class pbf_nf_fragment extends nf_fragment {
    static {
        layid = R.id.pbf;
        lay = R.layout.pbf_feed;
        lvid = R.id.listpbf;
    }

    @Override
    protected String getURL() {
        return "https://api.vk.com/method/wall.get?owner_id=-12998578&filter=all&count=100";
    }
}
