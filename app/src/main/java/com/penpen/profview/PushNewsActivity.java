package com.penpen.profview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by penpen on 01.11.15.
 */
public class PushNewsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_news);
Log.d("b", "started");
    }
}
