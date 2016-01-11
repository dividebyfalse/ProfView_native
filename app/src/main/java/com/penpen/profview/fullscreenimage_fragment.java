package com.penpen.profview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by penpen on 12.01.16.
 */
public class fullscreenimage_fragment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View view = inflater.inflate(R.layout.fullimage_fragment, container, false);
        Intent bundle = getIntent();
        if (bundle != null && !bundle.getStringExtra("img").equals("")) {
            new getIMG().execute(bundle.getStringExtra("img"));
        }
        setContentView(R.layout.fullimage_fragment);
        //return view;
    }


    class getIMG extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap img = null;
            try {
                InputStream in = new java.net.URL(params[0]).openStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            try {
                ImageView iv = (ImageView) findViewById(R.id.imageView);
                iv.setImageBitmap(result);
                PhotoViewAttacher mAttacher = new PhotoViewAttacher(iv);
            } catch (Exception ignored) {}
        }
    }
}
