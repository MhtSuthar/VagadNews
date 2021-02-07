package com.vagad.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Android-132 on 11-Feb-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private RSSParser rssParser = new RSSParser();
    private Context mContext;
    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        if(!AlarmUtils.isAlarmOn(AlarmUtils.ALARM_ID_FOR_NEWS, context)){
            AlarmUtils.setAlarm(context);
        }

        if(!AlarmUtils.isAlarmOn(AlarmUtils.ALARM_ID_FOR_DELETE_NEWS, context)){
            AlarmUtils.setAlarmForDeleteNews(context);
        }

        if(AppUtils.isOnline(mContext))
            new LoadRSSFeed().execute();

        checkAnyUpdate();
    }

    private void checkAnyUpdate() {
        if(AppUtils.isOnline(mContext)){
            try {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_VERSION);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        SharedPreferenceUtil.putValue(Constants.KEY_APP_VERSION, "" + dataSnapshot.getValue());
                        SharedPreferenceUtil.save();
                        if (Double.parseDouble(SharedPreferenceUtil.getString(Constants.KEY_APP_VERSION, "1.0")) > Double.parseDouble(BuildConfig.VERSION_NAME)) {
                            generateNotification(mContext, mContext.getString(R.string.update_avail) + " " + mContext.getString(R.string.update_message));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read value.", error.toException());
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
	


    /**
     * Background Async Task to get Education RSS data from URL
     **/
    class LoadRSSFeed extends AsyncTask<String, String, String> {

        private RSSDatabaseHandler rssDatabaseHandler;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rssDatabaseHandler = new RSSDatabaseHandler(mContext);
        }

        /**
         * getting Inbox JSON
         * */


        @Override
        protected String doInBackground(String... args) {
            try {
                List<RSSItem> rssFeed = rssParser.getRSSFeedItems(mContext.getString(R.string.feed_url_dungarpur));
                rssFeed.addAll(rssParser.getRSSFeedItems(mContext.getString(R.string.feed_url_banswara)));
                rssFeed.addAll(rssParser.getRSSFeedItems(mContext.getString(R.string.feed_url_udaipur)));
                for (int i = 0; i < rssFeed.size(); i++) {
                    String imgRegex = "<[iI][mM][gG][^>]+[sS][rR][cC]\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
                    Pattern p = Pattern.compile(imgRegex);
                    Matcher m = p.matcher(rssFeed.get(i).getDescription());
                    if (m.find()) {
                        try {
                            String imgSrc = m.group(1);
                            rssFeed.get(i).setImage(imgSrc);
                            if(rssFeed.get(i).getDescription().contains("/>") && rssFeed.get(i).get_news_type().equals(Constants.NEWS_TYPE_LATEST))
                                rssFeed.get(i).setDescription(rssFeed.get(i).getDescription().split("/>")[1]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    rssDatabaseHandler.addFeed(rssFeed.get(i));
                }

            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
            return null;
        }

    }

    void generateNotification(Context context, String title) {
        String summaryText = title;

        Intent resultIntent = new Intent(context, NewsListActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(summaryText);
        bigText.setBigContentTitle(context.getString(R.string.app_name));
        //bigText.setSummaryText("");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        notification = new NotificationCompat.Builder(context)
                .setCategory(Notification.CATEGORY_PROMO)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(summaryText)
                .setSmallIcon(getNotificationIcon())
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(icon1)
                .setAutoCancel(true)
                .setStyle(bigText)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND).build();
        mNotificationManager.notify(105, notification);
    }

    int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_white_notification : R.mipmap.ic_launcher;
    }

}
