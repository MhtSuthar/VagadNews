package com.vagad.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.VagadApp;
import com.vagad.busroute.fragment.BusRouteSearchFragment;
import com.vagad.dashboard.fragments.NewsListFragment;
import com.vagad.localnews.AddNewsActivity;
import com.vagad.localnews.fragment.ReporterNewsListFragment;
import com.vagad.model.NewsPostModel;
import com.vagad.model.TokenModel;
import com.vagad.receiver.NetworkChangeReceiver;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AppUtils;
import com.vagad.utils.BottomNavigationViewBehavior;
import com.vagad.utils.Constants;
import com.vagad.utils.fonts.CustomFontTextView;
import com.vagad.utils.rating.RateItDialogFragment;

/**
 * Created by Mohit on 15-Feb-17.
 */

public class NewsListActivity extends BaseActivity {

    private ViewPager viewPager;
    private static final String TAG = "NewsListActivity";
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private int[] mImages = new int[]{R.drawable.splash_bg, R.drawable.help_two, R.drawable.help_three, R.drawable.help_four};
    private InterstitialAd mInterstitialAd;
    private boolean mFullAddDisplayed;
    private int mVisiblePage = -1;
    private NewsListFragment newsListFragment = new NewsListFragment();
    private BusRouteSearchFragment busRouteSearchFragment = new BusRouteSearchFragment();
    private ReporterNewsListFragment reporterNewsListFragment = new ReporterNewsListFragment();
    private BottomNavigationView bottomNavigation;
    private CoordinatorLayout mCoordinatorLayout;
    private AdView adView;
    private int mBottomNavHeight = 120;
    private RelativeLayout mRelBottomMenu;
    private NetworkChangeReceiver mNetworkReceiver;
    private BottomSheetBehavior behavior;
    private boolean isNotificationFromLocaleNews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        isNotificationFromLocaleNews = getIntent().getBooleanExtra(Constants.EXTRA_FROM_LOCALE_NEWS, false);
        initView();

        initAds();

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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isNotificationFromLocaleNews = intent.getBooleanExtra(Constants.EXTRA_FROM_LOCALE_NEWS, false);
        if(viewPager != null)
            viewPager.setCurrentItem(isNotificationFromLocaleNews ? 2 : 0);
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
        }else{
            checkWhatsNew();
        }
    }

    private void checkWhatsNew() {
        double currentVersionNumber = 1.0;
        double savedVersionNumber = Double.parseDouble(SharedPreferenceUtil.getString(Constants.KEY_SAVED_VERSION, "1.0"));
        try {
            currentVersionNumber = Double.parseDouble(BuildConfig.VERSION_NAME);
        } catch (Exception e) {}
        if (currentVersionNumber > savedVersionNumber) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("What's New");
            builder.setMessage(getString(R.string.whats_new_texts));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            SharedPreferenceUtil.putValue(Constants.KEY_SAVED_VERSION, ""+currentVersionNumber);
            SharedPreferenceUtil.save();
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


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adView = (AdView) findViewById(R.id.adView);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mRelBottomMenu = (RelativeLayout) findViewById(R.id.relBottomMenu);
        setViewPagerAdapter(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mVisiblePage = position;
                switch (position){
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.menu_news);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.menu_bus);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.menu_event);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigation.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationViewBehavior());
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_news:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_bus:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_event:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        bottomNavigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomNavigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mBottomNavHeight = bottomNavigation.getHeight(); //height is ready
                Log.e(TAG, "onGlobalLayout: "+ bottomNavigation.getHeight());
            }
        });

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
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

    public void showBottomnavigation(){
        mRelBottomMenu.animate()
                .translationYBy(mBottomNavHeight)
                .translationY(0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    public void hideBottomnavigation(){
        mRelBottomMenu.animate()
                .translationYBy(0)
                .translationY(mBottomNavHeight)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mNetworkReceiver = new NetworkChangeReceiver();
            registerReceiver(mNetworkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
        }
    }

    public void setViewPagerAdapter(ViewPager viewPager){
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return newsListFragment;
                    case 1:
                        return busRouteSearchFragment;
                    case 2:
                        return reporterNewsListFragment;
                }
               return null;
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
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(isNotificationFromLocaleNews ? 2 : 0);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded() && !mFullAddDisplayed) {
            mInterstitialAd.show();
            mFullAddDisplayed = true;
        }else
            super.onBackPressed();
    }

    public void openMoreNews() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void onClickEntertainment(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_ENTERTAINMENT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickSport(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_SPORT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickAstrology(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_ASTROLOGY);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickEducation(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_EDUCATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickWorld(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_WORD);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickPolitics(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_POLITICS);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickBollyWood(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_BOLLYWOOD);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickHealth(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_HEALTH);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
