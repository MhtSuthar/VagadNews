package com.vagad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.NotificationUtils;

import java.util.List;


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
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_VERSION);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SharedPreferenceUtil.putValue(Constants.KEY_APP_VERSION, ""+dataSnapshot.getValue());
                    SharedPreferenceUtil.save();
                    if(Double.parseDouble(SharedPreferenceUtil.getString(Constants.KEY_APP_VERSION, "1.0")) > Double.parseDouble(BuildConfig.VERSION_NAME)){
                        new NotificationUtils().generateNotification(mContext, mContext.getString(R.string.update_avail)+" "+mContext.getString(R.string.update_message));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read value.", error.toException());
                }
            });
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
                List<RSSItem> rssFeed = rssParser.getRSSFeedItems(mContext.getString(R.string.feed_url_education));
                for (int i = 0; i < rssFeed.size(); i++) {
                    rssDatabaseHandler.addFeed(rssFeed.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
            return null;
        }

    }

}
