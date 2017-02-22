package com.vagad.prestart;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.HomeActivity;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;
import com.vagad.utils.loder.CircleProgressBar;

import java.util.List;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIMEOUT = 2000;
    private CircleProgressBar progressBar;
    private RSSDatabaseHandler rssDatabaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        progressBar = (CircleProgressBar) findViewById(R.id.progressBar);

        fullScreen();

        if(isOnline(this) && !rssDatabaseHandler.isDataAvailable()){
            new LoadRSSFeed().execute();
        }else {
            startTimer();
        }

        setAlarm();

        setCurrantTimestamp();
    }

    /*
      For delete news after 5 days
     */
    private void setCurrantTimestamp() {
        if(SharedPreferenceUtil.getString(Constants.KEY_STARTUP_TIME, "").equals("")){
            SharedPreferenceUtil.putValue(Constants.KEY_STARTUP_TIME, DateUtils.getTimestamp());
            SharedPreferenceUtil.save();
        }
    }

    private void setAlarm() {
        if(!SharedPreferenceUtil.getBoolean(Constants.KEY_IS_ALARM_SETUP, false)){
            AlarmUtils.setAfterFiveDaysAlarm(this);
            AlarmUtils.setAlarm(this);
        }
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

    /**
     * Background Async Task to get RSS data from URL
     * */
    class LoadRSSFeed extends AsyncTask<String, String, String> {
        private RSSParser rssParser = new RSSParser();
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        /**
         * getting Inbox JSON
         * */
        @Override
        protected String doInBackground(String... args) {
            try {
                List<RSSItem> rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_dungarpur));
                rssFeed.addAll(rssParser.getRSSFeedItems(getString(R.string.feed_url_banswara)));
                rssFeed.addAll(rssParser.getRSSFeedItems(getString(R.string.feed_url_udaipur)));
                rssFeed.addAll(rssParser.getRSSFeedItems(getString(R.string.feed_url_latest_news)));
                for (int i = 0; i < rssFeed.size(); i++) {
                    rssDatabaseHandler.addFeed(rssFeed.get(i));
                }

            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            showProgress(false);
            List<RSSItem> mNewsList = rssDatabaseHandler.getAllSites();
            if(mNewsList.size() > 0){
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }

    }

    private void showProgress(boolean b) {
        if(b){
            AnimationUtils.animateScaleOut(progressBar);
        }else{
            AnimationUtils.animateScaleIn(progressBar);
        }
    }


}
