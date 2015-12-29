package adapter;

import app.FeedImageView;

import app.AppController;
import data.FeedItem;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.penpen.profview.MainActivity;
import com.penpen.profview.R;

/**
 * Created by penpen on 08.10.15.
 */
public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ImageView linkpic = (ImageView) convertView.findViewById(R.id.imgUrl);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.nl);
        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("newsid", feedItems.get(position).getNewsid());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                try {
                    pendingIntent.send();
                } catch (Exception e) {

                }*/
            }
        };
        ll.setOnClickListener(cl);
        //statusMsg.setOnClickListener(cl);

        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);

        FeedItem item = feedItems.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            SpannableString ss = new SpannableString(item.getStatus().replaceAll("\\[(id[0-9]+|club[0-9]+)\\|", "").replace("]", ""));
            Pattern vkpattern = Pattern.compile("\\[(id[0-9]+|club[0-9]+)\\|");
            Matcher vkmatcher = vkpattern.matcher(item.getStatus());
                int i = 0;
                boolean ismf = false;
                int offset=0;
                int pcorrect = 0;
                int correct=0;
                while (vkmatcher.find()) {
                    ismf = true;
                    final String link = "http://vk.com/" + vkmatcher.group(i).substring(1, vkmatcher.group(i).length()-1);
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(link));
                            activity.startActivity(intent);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            //ds.setUnderlineText(false);
                        }
                    };

                    correct+=vkmatcher.group(i).length();
                    ss.setSpan(clickableSpan, item.getStatus().indexOf(vkmatcher.group(i), offset)-pcorrect, item.getStatus().indexOf("]", item.getStatus().indexOf(vkmatcher.group(i), offset)+ vkmatcher.group(i).length())- correct, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    correct+=1;
                    pcorrect+=vkmatcher.group(i).length()+1;
                    offset = item.getStatus().indexOf(vkmatcher.group(i), offset);
                    //Log.d("urlmatch", vkmatcher.group(i));
                    }
                if (ismf) {
                    statusMsg.setText(ss);
                    statusMsg.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    statusMsg.setText(item.getStatus());
            }
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">Подробнее...</a> "));
            final String link = item.getUrl();
            // Making url clickable
            linkpic.setVisibility(View.VISIBLE);
            linkpic.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(link));
                    activity.startActivity(intent);
                }
            });
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);

        } else {
            // url is null, remove from the view
            linkpic.setVisibility(View.GONE);
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
