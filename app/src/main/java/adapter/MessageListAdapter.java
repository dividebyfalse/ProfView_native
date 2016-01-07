package adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penpen.profview.MainActivity;
import com.penpen.profview.R;
import com.penpen.profview.push_message_list_fragment;

import java.util.List;

import data.MessageItem;

/**
 * Created by penpen on 27.11.15.
 */
public class MessageListAdapter extends BaseAdapter {
    private FragmentActivity activity;
    private LayoutInflater inflater;
    private List<MessageItem> messagesItems;
    private BaseAdapter adapter;


    public MessageListAdapter(FragmentActivity activity, List<MessageItem> messagesItems) {
        this.activity = activity;
        this.messagesItems = messagesItems;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int location) {
        return messagesItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.message_item, null);
        LinearLayout backlayout = (LinearLayout) convertView.findViewById(R.id.backmessagelay);
        TextView message = (TextView) convertView.findViewById(R.id.message_text);
        TextView date = (TextView) convertView.findViewById(R.id.message_date);
        MessageItem item = messagesItems.get(position);
        message.setText(item.getMessage());
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getDate())*1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        date.setText(timeAgo);
        if (item.getIsnew()) {
            backlayout.setBackgroundColor(Color.parseColor("#FF819B9D"));
        } else {
            backlayout.setBackgroundColor(Color.parseColor("#bfcfd0"));
        }

        Button del = (Button) convertView.findViewById(R.id.deletemessage);
        adapter = this;
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper= new DBHelper(v.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String selection = "date = ?";
                String[]selectionArgs = new String[] { messagesItems.get(position).getDate() };
                db.delete("pushmessagetable", selection, selectionArgs);
                messagesItems.remove(position);
                adapter.notifyDataSetChanged();
                db.close();
                dbHelper.close();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new push_message_list_fragment())
                        .commit();
            }
        });

        backlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(v.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                String selection = "date = ?";
                String[] selectionArgs = new String[]{messagesItems.get(Integer.parseInt(String.valueOf(position))).getDate()};
                cv.put("isnew", 0);
                db.update("pushmessagetable", cv, selection, selectionArgs);
                messagesItems.get(Integer.parseInt(String.valueOf(position))).setIsnew(false);
                adapter.notifyDataSetChanged();

                Fragment mf = new push_message_list_fragment();
                Bundle args = new Bundle();
                args.putString("message", messagesItems.get(position).getMessage());
                args.putString("date", messagesItems.get(position).getDate());
                args.putInt("position", position);

                if (messagesItems.get(position).getIsnews()) {
                    args.putBoolean("isnews", true);
                    args.putString("mainimage", messagesItems.get(position).getImage());
                    args.putString("pi", messagesItems.get(position).getProfile_pic());
                    args.putString("pn", messagesItems.get(position).getProfile_name());
                }
                mf.setArguments(args);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mf)
                        .addToBackStack(null)
                        .commit();
                MainActivity ma = (MainActivity) activity;
                ma.menustack.add(2);
                db.close();
                dbHelper.close();
            }
        });
        return convertView;
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table pushmessagetable ("
                    + "id integer primary key autoincrement,"
                    + "message text,"
                    + "date text,"
                    + "isnew int,"
                    + "isnews int,"
                    + "groupimage text,"
                    + "mainimage text,"
                    + "groupdesc text"+");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
