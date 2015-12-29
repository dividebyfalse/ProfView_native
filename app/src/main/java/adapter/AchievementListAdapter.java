package adapter;

/**
 * Created by penpen on 24.11.15.
 */
import app.authorization;
import data.AchievementItem;
import com.penpen.profview.R;
import com.penpen.profview.achievement_fragment;
import com.penpen.profview.achievements_fragment;
import com.penpen.profview.login_fragment;
import com.penpen.profview.push_message_list_fragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AchievementListAdapter extends BaseAdapter {
    private FragmentActivity activity;
    private LayoutInflater inflater;
    private List<AchievementItem> achievementsItems;
    private BaseAdapter adapter;
    private int position;
    private String category;
    private String subcategory;

    public AchievementListAdapter(FragmentActivity activity, List<AchievementItem> achievementsItems) {
        this.activity = activity;
        this.achievementsItems = achievementsItems;
    }

    @Override
    public int getCount() {
        return achievementsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return achievementsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.position = position;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.achievement_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.achievement_name);
        TextView date = (TextView) convertView.findViewById(R.id.achievement_date);
        TextView category = (TextView) convertView.findViewById(R.id.achievement_category);
        TextView dateadd = (TextView) convertView.findViewById(R.id.achievement_date_of_add);
        LinearLayout prem = (LinearLayout) convertView.findViewById(R.id.achievement_prem);
        LinearLayout stip = (LinearLayout) convertView.findViewById(R.id.achievement_stip);
        Button del = (Button) convertView.findViewById(R.id.ach_delete);
        Button edit = (Button) convertView.findViewById(R.id.ach_edit);
        adapter = this;
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AchievementItem ai = achievementsItems.get(position);
                new getEditAchievement().execute(ai.getAchid(),
                                                ai.getName(),
                                                ai.getDate());
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AchievementItem ai = achievementsItems.get(position);
                new removeAchievement().execute(ai.getAchid(),
                        ai.getName(),
                        ai.getDate());
            }
        });

        AchievementItem item = achievementsItems.get(position);

        name.setText(item.getName());
        date.setText(item.getDate());
        category.setText(item.getCategory());
        dateadd.setText(item.getDateofadd());
        switch (item.getIsprem()) {
            case 0:
                prem.setBackgroundColor(Color.parseColor("#008000"));
                break;
            case 1:
                prem.setBackgroundColor(Color.parseColor("#FFA605"));
                break;
            case 2:
                prem.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
        }

        switch (item.getIsstip()) {
            case 0:
                stip.setBackgroundColor(Color.parseColor("#008000"));
                break;
            case 1:
                stip.setBackgroundColor(Color.parseColor("#FFA605"));
                break;
            case 2:
                stip.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
        }

        return convertView;
    }

    class getEditAchievement extends AsyncTask<String, Void, Boolean> {
        private HttpURLConnection connection;
        private ProgressDialog dialog;
        private String achid;
        private String name;
        private String date;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(activity, "Подождите", "Загрузка...", true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String result = "";
            BufferedReader reader = null;
            achid = params[0];
            name = params[1];
            date = params[2];
            try {
                URL url = new URL("http://irk.yourplus.ru/rating/achievements/edit/"+params[0]+"/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.addRequestProperty("Cookie", authorization.cookie);
                connection.addRequestProperty("Upgrade-Insecure-Requests", "1");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
                connection.disconnect();
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
            Document doc = Jsoup.parse(result);
            category = doc.getElementById("j-award-19096-type").attr("value");
            subcategory = doc.getElementById("j-award-19097-type").attr("value");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if (result) {
                Fragment nf = new achievement_fragment();
                Bundle args = new Bundle();
                args.putString("name", name);
                args.putString("date", date);
                args.putInt("position", position);
                args.putString("category", category);
                args.putString("subcategory", subcategory);
                args.putString("id", achid);
                nf.setArguments(args);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, nf)
                        .commit();
            }
        }

    }

    class removeAchievement extends AsyncTask<String, Void, Boolean> {
        private HttpURLConnection connection;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(activity, "Подождите", "Идет удаление...", true);
        }

        private String getCategory(String URL) {
            String result = "";
            BufferedReader reader = null;
            try {
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.addRequestProperty("Cookie", authorization.cookie);
                connection.addRequestProperty("Upgrade-Insecure-Requests", "1");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
                connection.disconnect();
            }catch(Exception e){
                e.printStackTrace();
            }
            Document doc = Jsoup.parse(result);
            return doc.getElementById("j-award-19096-type").attr("value");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
                     /*if (authorization.cookie.length() != 0) {
                         result = true;
                     } else {*/
            String response = "";
            response = authorization.auth(activity);
            try {
                if ((!response.equals("error")) && (!response.equals("no_login")) && (response.length() != 0)) {
                    result = true;
                } else if (response.equals("no_login")) {
                    result = false;
                }
            } catch (Exception e) {
                result = true;
            }
            //}
            if (result) {
                String line;
                StringBuffer jsonString = new StringBuffer();
                String category = getCategory("http://irk.yourplus.ru/rating/achievements/edit/"+params[0]+"/");
                try {
                    URL url = new URL("http://irk.yourplus.ru/rating/achievements/edit/"+params[0]+"/");
                    String payload = "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"ID\"\n" +
                            "\n" +
                            params[0]+"\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"action\"\n" +
                            "\n" +
                            "delete\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"NAME\"\n" +
                            "\n" +
                            params[1]+"\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"DATE\"\n" +
                            "\n" +
                            params[2]+"\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"award[19096][type]\"\n" +
                            "\n" +
                            category + "\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"award[19097][type]\"\n" +
                            "\n" +
                            category + "\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9\n" +
                            "Content-Disposition: form-data; name=\"files[]\"; filename=\"\"\n" +
                            "Content-Type: application/octet-stream\n" +
                            "\n" +
                            "\n" +
                            "------WebKitFormBoundaryOmrCX6ln9N3314s9--";
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryOmrCX6ln9N3314s9");
                    connection.setRequestProperty("Cookie", response);
                    connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                    writer.write(payload);
                    writer.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line);
                    }
                    br.close();
                    connection.disconnect();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }

                if (!jsonString.toString().equals("")) {
                    Document doc = Jsoup.parse(jsonString.toString());
                    Elements el = doc.body().getElementsByClass("b-form-success");
                    for (int i =0; i<el.size(); i++) {
                        if (!el.get(i).toString().equals("")) {
                            return true;
                        }
                    }
                }
                return false;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                dialog.dismiss();
                achievementsItems.remove(position);
                adapter.notifyDataSetChanged();
                if (achievementsItems.size() == 0) {
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.achievements_layout);
                    LinearLayout na = (LinearLayout) activity.findViewById(R.id.empty_ach);
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                    na.setVisibility(View.VISIBLE);
                }
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new achievements_fragment())
                        .commit();
                Toast toast = Toast.makeText(activity.getApplicationContext(), "Достижение удалено", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
