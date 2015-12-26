package com.penpen.profview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by penpen on 26.12.15.
 */
public class news_item_fragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.news_item_fragment, container, false);

        final Bundle  bundle = getArguments();
        if (bundle != null) {
            new getFullItem().execute(bundle.getString("newsid", ""));
        }

        return view;
    }


    class getFullItem extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected String doInBackground(String... params) {
            String JSON = "";
            try {
                URL url = new URL("https://api.vk.com/method/wall.getById?posts="+params[0]+"&extended=1&copy_history_depth=2&v=5.41");
                Log.d("url", "https://api.vk.com/method/wall.getById?posts="+params[0]+"&extended=1&copy_history_depth=2&v=5.41");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(3000);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                JSON = buffer.toString();
                urlConnection.disconnect();
            }catch(Exception e){
                e.printStackTrace();
            }
            return JSON;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                String date = "";
                String text = "";
                String image = "";
                String profile_pic = "";
                String profile_name = "";
                try {
                    JSONObject dataJsonObj = new JSONObject(result).getJSONObject("response");
                    JSONArray items = dataJsonObj.getJSONArray("items");
                    date = items.getJSONObject(0).getString("date");
                    if (items.getJSONObject(0).has("copy_history")) {
                        text = items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getString("text");
                        if (items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).has("attachments") && items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").length() > 0 && items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").getJSONObject(0).has("photo")) {
                            if (items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_807")) {
                                image = items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_807");
                            } else if (items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_604")) {
                                image = items.getJSONObject(0).getJSONArray("copy_history").getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_604");
                            }
                        }
                    } else {
                        text = items.getJSONObject(0).getString("text");
                        if (items.getJSONObject(0).has("attachments") && items.getJSONObject(0).getJSONArray("attachments").length() > 0 && items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).has("photo")) {
                            if (items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_807")) {
                                image = items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_807");
                            } else if (items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").has("photo_604")) {
                                image = items.getJSONObject(0).getJSONArray("attachments").getJSONObject(0).getJSONObject("photo").getString("photo_604");
                            }
                        }
                    }
                    profile_name = dataJsonObj.getJSONArray("groups").getJSONObject(0).getString("name");
                    profile_pic = dataJsonObj.getJSONArray("groups").getJSONObject(0).getString("photo_200");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                        Long.parseLong(date)*1000,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                TextView gn = (TextView) view.findViewById(R.id.news_group_name);
                TextView tn = (TextView) view.findViewById(R.id.news_text);
                TextView dn = (TextView) view.findViewById(R.id.news_date);
                ImageView gi = (ImageView) view.findViewById(R.id.news_group_image);
                ImageView mi = (ImageView) view.findViewById(R.id.news_image);
                gn.setText(profile_name);
                tn.setText(text);
                dn.setText(timeAgo);
                BitmapDownloaderTask task = new BitmapDownloaderTask(gi);
                task.execute(profile_pic);
                BitmapDownloaderTask tasks = new BitmapDownloaderTask(mi);
                tasks.execute(image);
            }
        }
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            Bitmap mib = null;
            try {
                InputStream in = new java.net.URL(params[0]).openStream();
                mib = BitmapFactory.decodeStream(in);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return mib;
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
