package com.penpen.profview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by penpen on 02.11.15.
 */
public class PushMessageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_message);
        Intent intent = getIntent();

        TextView tv = (TextView) findViewById(R.id.pushnewstext);
        tv.setText(intent.getStringExtra("text"));
    }
}
