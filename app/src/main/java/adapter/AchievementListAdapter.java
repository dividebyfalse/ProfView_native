package adapter;

/**
 * Created by penpen on 24.11.15.
 */
import data.AchievementItem;
import com.penpen.profview.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class AchievementListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<AchievementItem> achievementsItems;

    public AchievementListAdapter(Activity activity, List<AchievementItem> achievementsItems) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

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
}
