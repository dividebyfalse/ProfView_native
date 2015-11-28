package adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
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

import java.util.List;

import data.MessageItem;

/**
 * Created by penpen on 27.11.15.
 */
public class MessageListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageItem> messagesItems;
    private BaseAdapter adapter;


    public MessageListAdapter(Activity activity, List<MessageItem> messagesItems) {
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
                db.close();
                dbHelper.close();
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("ShowMessageItem", true);
                intent.putExtra("message", messagesItems.get(position).getMessage());
                intent.putExtra("date", messagesItems.get(position).getDate());
                intent.putExtra("position", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                try {
                    pendingIntent.send();
                } catch (Exception e) {

                }
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
            Log.d("log", "--- onCreate database ---");
            db.execSQL("create table pushmessagetable ("
                    + "id integer primary key autoincrement,"
                    + "message text,"
                    + "date text," +
                    "isnew int" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
