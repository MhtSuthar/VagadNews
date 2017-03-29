package com.vagad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.vagad.R;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AppUtils;

import java.util.List;


/**
 * Created by Android-132 on 11-Feb-16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private RSSParser rssParser = new RSSParser();
    private Context mContext;

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
