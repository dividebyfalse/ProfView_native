package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penpen.profview.MainActivity;
import com.penpen.profview.R;

import java.util.List;

import data.MenuItem;

/**
 * Created by penpen on 07.01.16.
 */
public class MenuListAdapter extends BaseAdapter {
    private List<MenuItem> menuItems;
    private Activity activity;
    private LayoutInflater inflater;
    private int prevsel = -1;

    public MenuListAdapter(Activity activity, List<MenuItem> menuItems) {
        this.menuItems = menuItems;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int location) {
        return menuItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.sidemenu_item, null);
        LinearLayout smi = (LinearLayout) convertView.findViewById(R.id.smi);
        LinearLayout sel = (LinearLayout) convertView.findViewById(R.id.sel);
        MainActivity ma = (MainActivity) activity;
        if (prevsel != -1 && prevsel != ma.menuselected) {
            menuItems.get(prevsel).setSelected(false);
        }
        if (menuItems.get(position).getSelected() != null && menuItems.get(position).getSelected()) {
            sel.setVisibility(View.VISIBLE);
            smi.setBackgroundResource(R.drawable.sidemenu_item_background_focuded);
            prevsel = position;
        } else {
            sel.setVisibility(View.INVISIBLE);
            smi.setBackgroundResource(R.drawable.sidemenu_item_background);
        }
        TextView desc = (TextView) convertView.findViewById(R.id.text);
        desc.setText(menuItems.get(position).getDescription());

        return convertView;
    }
}
