package com.vagad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.vagad.R;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private RSSDatabaseHandler rssDatabaseHandler;
    private Context context;
    private NotificationUtils notificationUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        rssDatabaseHandler = new RSSDatabaseHandler(context);
        notificationUtils = new NotificationUtils();
        if (AppUtils.isOnline(context)) {
            new LoadRSSFeed().execute();
        } else {
            setNotifyOfflineNews(context);
            AlarmUtils.setAlarm(context);
        }
    }

    private void setNotifyOfflineNews(final Context context) {
        final List<RSSItem> mNewsList = rssDatabaseHandler.getAllSites();
        if (mNewsList.size() > 2) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        notificationUtils.generateNotification(context, mNewsList.get(i).getTitle());
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
        }
    }


    class LoadRSSFeed extends AsyncTask<String, String, List<RSSItem>> {

        private RSSParser rssParser = new RSSParser();

        @Override
        protected List<RSSItem> doInBackground(String... args) {
            List<RSSItem> rssFeed = new ArrayList<>();
            try {
                rssFeed.addAll(rssParser.getRSSFeedItems(context.getString(R.string.feed_news18_rajasthan)));
                rssFeed.addAll(rssParser.getRSSFeedItems(context.getString(R.string.feed_url_banswara)));
                rssFeed.addAll(rssParser.getRSSFeedItems(context.getString(R.string.feed_url_udaipur)));
                rssFeed.addAll(rssParser.getRSSFeedItems(context.getString(R.string.feed_url_dungarpur)));
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
            } catch (Exception e) {
                e.printStackTrace();
                return rssFeed;
            }
            return rssFeed;
        }

        @Override
        protected void onPostExecute(final List<RSSItem> rssItems) {
            super.onPostExecute(rssItems);
            Handler handler = new Handler();
            if (rssItems.size() > 2) {
                for (int i = 0; i < 2; i++) {
                    final int val = i;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notificationUtils.showBigNotification(rssItems.get(val).getImage(), rssItems.get(val).getTitle(), context);
                        }
                    }, 1000 * i);
                }
            }
            AlarmUtils.cancelAlarm(context, AlarmUtils.ALARM_ID_FOR_NEWS);
            AlarmUtils.setAlarm(context);
        }
    }

}
