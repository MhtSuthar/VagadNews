package com.vagad.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.BaseFragment;
import com.vagad.base.VagadApp;
import com.vagad.dashboard.adapter.NewsRecyclerAdapter;
import com.vagad.dashboard.fragments.HeaderNewsFragment;
import com.vagad.model.NewsPostModel;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.loder.CircleProgressBar;
import com.vagad.utils.pageindicator.CirclePageIndicator;
import com.vagad.utils.rating.RateItDialogFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mohit on 15-Feb-17.
 */

public class NewsListActivity extends BaseActivity {

    private NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private CircleProgressBar progressBar;
    private RSSParser rssParser = new RSSParser();
    private static final String TAG = "NewsListActivity";
    private  RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CirclePageIndicator pageIndicator;
    private ProgressBar mProgressBarToolbar;
    private Handler handler = new Handler();
    private int delay = 5000;
    private int mVisiblePage = -1;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private ImageView imgNoData;
    private RelativeLayout mRelNoData;
    private int mStartLatestNews = 0, mEndLatestNews = 5;
    private List<RSSItem> mLatestNewsList = new ArrayList<>();
    private List<RSSItem> mLatestNewsListVisible = new ArrayList<>();
    private int[] mImages = new int[]{R.drawable.splash_bg, R.drawable.help_two, R.drawable.help_three, R.drawable.help_four};
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private boolean mFullAddDisplayed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_list);
        initView();
        initAds();
        setAllNews();
        if(isOnline(this))
            new LoadRSSFeed().execute();

        Bundle bundle = new Bundle();
        bundle.putString("Devices", "Mobile Used "+android.os.Build.MODEL);
        VagadApp.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        /**
         * Showing Rating Dialog
         */
        RateItDialogFragment.show(this, getSupportFragmentManager());

        /**
         * Check forcefully Update
         */
        checkUpdateAvail();

        addValToFirebase();

        startActivity(new Intent(this, HomeActivity.class));
    }

    private void addValToFirebase() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);

        String userId = mDatabase.push().getKey();

        NewsPostModel user = new NewsPostModel("Mht", "rmht.info");

        //mDatabase.child(userId).setValue(user);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: "+dataSnapshot.getKey()+"   "+dataSnapshot.getRef()+""+dataSnapshot.getChildren()+"   "+dataSnapshot.getChildrenCount());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    NewsPostModel changedPost = messageSnapshot.getValue(NewsPostModel.class);
                    Log.e(TAG, "for : "+changedPost.email);
                    /*String name = (String) messageSnapshot.child("email").getValue();
                    String message = (String) messageSnapshot.child("username").getValue();
                    Log.e(TAG, "for loop: "+name+"  "+message);*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void checkUpdateAvail() {
        if(Double.parseDouble(SharedPreferenceUtil.getString(Constants.KEY_APP_VERSION, "1.0")) > Double.parseDouble(BuildConfig.VERSION_NAME)){
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getString(R.string.update_avail));
            builder.setCancelable(false);
            builder.setMessage(getString(R.string.update_message));
            builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                }
            });
            builder.show();
        }
    }

    private void initAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        adView.loadAd(adRequest);

        if(!isOnline(this))
            adView.setVisibility(View.GONE);

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setAllNews() {
        mNewsList = rssDatabaseHandler.getAllSites();
        mLatestNewsList = rssDatabaseHandler.getLatestNews();
        if(mNewsList.size() > 0){
            imgNoData.setVisibility(View.GONE);
            setRecyclerAdapter();
            setViewPagerAdapter(viewPager);
        }else{
            mRelNoData.setVisibility(View.VISIBLE);
            imgNoData.setImageResource(mImages[new Random().nextInt(mImages.length)]);
        }
    }

    public void setAllNewsForHeaderFavChanges() {
        mNewsList = rssDatabaseHandler.getAllSites();
        if(mNewsList.size() > 0){
            imgNoData.setVisibility(View.GONE);
            setRecyclerAdapter();
            setViewPagerAdapter(viewPager);
        }
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        imgNoData = (ImageView) findViewById(R.id.imgNoData);
        mRelNoData = (RelativeLayout) findViewById(R.id.relNoData);
        mProgressBarToolbar = (ProgressBar) findViewById(R.id.progressBarToolbar);
        adView = (AdView) findViewById(R.id.adView);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_favourite:
                        Intent intent = new Intent(NewsListActivity.this, FavListActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivityForResult(intent, Constants.REQUEST_CODE_FAV_NEWS,  ActivityOptions.makeSceneTransitionAnimation(NewsListActivity.this).toBundle());
                        }else{
                            startActivityForResult(intent, Constants.REQUEST_CODE_FAV_NEWS);
                        }
                        break;
                    case R.id.menu_about_us:
                        moveActivity(new Intent(NewsListActivity.this, AboutUsActivity.class), NewsListActivity.this, false);
                        break;
                    case R.id.menu_feedback:
                        sendFeedback();
                        break;
                    case R.id.menu_share:
                        shareApp();
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.home_menu);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mVisiblePage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

       /* recyclerView.addOnItemTouchListener(new RecyclerTouchListener(recyclerView, new RecyclerTouchListener.OnRecyclerClickListener() {
            @Override
            public void onClick(View v, int position) {
               openNewsDetail(mNewsList.get(position));
            }
            @Override
            public void onLongClick(View v, int position) {

            }
        }));*/
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return true;
    }

    private void sendFeedback() {
        ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .addEmailTo(getString(R.string.my_email))
                .setSubject("Vagad App Feedback")
                .setText("")
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle("Your Feedback")
                .startChooser();
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my Vagad News App at: https://play.google.com/store/apps/details?id=com.vagad");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void openNewsDetail(RSSItem rssItem, ImageView imageView, int position) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        //intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageView, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: "+resultCode+"  "+requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_CODE_NEWS_DETAIL){
               changeInFav(data);
            }else if(requestCode == Constants.REQUEST_CODE_FAV_NEWS){
                setAllNews();
            }
        }
    }

    private void changeInFav(Intent data) {
        RSSItem rssItem = data.getParcelableExtra(Constants.Bundle_Feed_Item);
        for (int i = 0; i < mNewsList.size(); i++) {
            if(mNewsList.get(i).getId() == rssItem.getId()){
                mNewsList.set(i, rssItem);
                break;
            }
        }
        newsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }


    Runnable runnable = new Runnable() {
        public void run() {
            if (mLatestNewsListVisible.size()-1 == mVisiblePage) {
                mVisiblePage = 0;
                if(mEndLatestNews >= mLatestNewsList.size()){
                    mStartLatestNews = 0;
                    mEndLatestNews = 5;
                }else {
                    mStartLatestNews = mStartLatestNews + 5;
                    mEndLatestNews = mEndLatestNews + 5;
                }
                handler.removeCallbacks(runnable);
                Log.e(TAG, "end: "+mStartLatestNews+"     "+mEndLatestNews);
                setViewPagerAdapter(viewPager);
            } else {
                mVisiblePage++;
                viewPager.setCurrentItem(mVisiblePage, true);
                handler.postDelayed(this, delay);
            }

        }
    };

    private void setRecyclerAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerAdapter = new NewsRecyclerAdapter(mNewsList, this, NewsListActivity.this);
        recyclerView.setAdapter(newsRecyclerAdapter);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        recyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        if(show){
            AnimationUtils.animateScaleOut(progressBar);
            //binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            AnimationUtils.animateScaleIn(progressBar);
        }
    }


    public void setViewPagerAdapter(ViewPager viewPager){
        final List<RSSItem> mRandomList = getRandomList();
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mRandomList.size();
            }
            @Override
            public Fragment getItem(int position) {
                HeaderNewsFragment headerNewsFragment = new HeaderNewsFragment();
                headerNewsFragment.setList(mLatestNewsListVisible);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.Bundle_Pos, position);
                bundle.putParcelable(Constants.Bundle_Feed_Item, mRandomList.get(position));
                headerNewsFragment.setArguments(bundle);
                return headerNewsFragment;
            }
            @Override
            public Parcelable saveState() {return null;}

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }
        };
        viewPager.setAdapter(mHeaderPagerAdapter);
        viewPager.setOffscreenPageLimit(mRandomList.size());
        viewPager.setCurrentItem(0);
        pageIndicator.setViewPager(viewPager);
        handler.postDelayed(runnable, delay);
    }

    private List<RSSItem> getRandomList() {
        mLatestNewsListVisible = new ArrayList<>();
        for (int i = mStartLatestNews; i < mEndLatestNews; i++) {
            if(i < mLatestNewsList.size())
                mLatestNewsListVisible.add(mLatestNewsList.get(i));
        }
        return mLatestNewsListVisible;
    }

    /**
     * Background Async Task to get RSS data from URL
     * */
    class LoadRSSFeed extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarToolbar.setVisibility(View.VISIBLE);
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
            mProgressBarToolbar.setVisibility(View.GONE);
            mNewsList.clear();
            mNewsList.addAll(rssDatabaseHandler.getAllSites());
            if(newsRecyclerAdapter == null){
                setRecyclerAdapter();
                setViewPagerAdapter(viewPager);
            }else {
                newsRecyclerAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded() && !mFullAddDisplayed) {
            mInterstitialAd.show();
            mFullAddDisplayed = true;
        }else
            super.onBackPressed();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
