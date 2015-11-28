package com.penpen.profview;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.AchievementListAdapter;
import adapter.MessageListAdapter;
import data.AchievementItem;
import data.MessageItem;

/**
 * Created by penpen on 27.11.15.
 */
public class push_message_list_fragment extends Fragment {
    View view;
    private DBHelper dbHelper;
    public List<MessageItem> messageItems;
    public MessageListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_list_layout, container, false);
        final Bundle  bundle = getArguments();
        final LinearLayout messageviewlay = (LinearLayout) view.findViewById(R.id.messageread);
        final LinearLayout messagelistlay = (LinearLayout) view.findViewById(R.id.messagelistlay);
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        messageItems = new ArrayList<MessageItem>();
        listAdapter = new MessageListAdapter(getActivity(), messageItems);
        ListView messagelist = (ListView) view.findViewById(R.id.messagelist);
        Cursor c = db.query("pushmessagetable", null, null, null, null, null, null);
        if (c.moveToLast()) {
            int dateColIndex = c.getColumnIndex("date");
            int messageColIndex = c.getColumnIndex("message");
            int isnewColIndex = c.getColumnIndex("isnew");
            do {
                Boolean isnew = false;
                if (c.getInt(isnewColIndex) == 1) {isnew =true;}
                MessageItem messageitem = new MessageItem(c.getString(messageColIndex),c.getString(dateColIndex), isnew);
                messageItems.add(messageitem);
            } while (c.moveToPrevious());
        } else
            Log.d("log", "0 rows");
        messagelist.setAdapter(listAdapter);
        c.close();
        dbHelper.close();

        if (bundle != null) {
            TextView messagedate = (TextView) view.findViewById(R.id.datemessage);
            TextView messageview = (TextView) view.findViewById(R.id.pushnewstext);
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(bundle.getString("date")) * 1000,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            messagedate.setText("Получено: " + timeAgo);
            messageview.setText(bundle.getString("message"));
            Button delmessage = (Button) view.findViewById(R.id.delmessage);
            delmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHelper dbHelper = new DBHelper(v.getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String selection = "date = ?";
                    String[] selectionArgs = new String[]{bundle.getString("date")};
                    db.delete("pushmessagetable", selection, selectionArgs);
                    messageItems.remove(bundle.getInt("position"));
                    db.close();
                    dbHelper.close();
                    messagelistlay.setVisibility(View.VISIBLE);
                    messageviewlay.setVisibility(View.INVISIBLE);
                }
            });
            messagelistlay.setVisibility(View.INVISIBLE);
            messageviewlay.setVisibility(View.VISIBLE);
        } else  {
            messagelistlay.setVisibility(View.VISIBLE);
            messageviewlay.setVisibility(View.INVISIBLE);
        }
        return view;
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
