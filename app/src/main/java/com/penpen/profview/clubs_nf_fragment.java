package com.penpen.profview;

/**
 * Created by penpen on 13.10.15.
 */
public class clubs_nf_fragment extends nf_fragment {
      static {
            lay = R.layout.clubs_feed;
            lvid = R.id.listcl;
      }

    @Override
    protected String getURL() {
        return "https://api.vk.com/method/wall.get?owner_id=-65955842&filter=all&count=100";
    }
}
