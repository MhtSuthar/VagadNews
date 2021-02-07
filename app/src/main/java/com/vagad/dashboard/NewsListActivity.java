package com.vagad.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.VagadApp;
import com.vagad.busroute.fragment.BusRouteSearchFragment;
import com.vagad.dashboard.fragments.NewsListFragment;
import com.vagad.localnews.fragment.ReporterNewsListFragment;
import com.vagad.receiver.NetworkChangeReceiver;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.Constants;
import com.vagad.utils.rating.RateItDialogFragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;

/**
 * Created by Mohit on 15-Feb-17.
 */

public class NewsListActivity extends BaseActivity {

    private ViewPager viewPager;
    private static final String TAG = "NewsListActivity";
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private NewsListFragment newsListFragment = new NewsListFragment();
    private ReporterNewsListFragment reporterNewsListFragment = new ReporterNewsListFragment();
    private BottomNavigationView bottomNavigation;
    private AdView adView;
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
            viewPager.setCurrentItem(isNotificationFromLocaleNews ? 1 : 0);
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adView = (AdView) findViewById(R.id.adView);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setViewPagerAdapter(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.menu_news);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.menu_event);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_news:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_event:
                        viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
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
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {
            @Override
            public int getCount() {
                return 2;
            }
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return newsListFragment;
                    case 1:
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
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(isNotificationFromLocaleNews ? 1 : 0);
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

    public void onClickRajasthan(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_RAJASTHAN);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickGadget(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_GADGET);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickFilmReview(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_FILM_REVIEW);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onClickIndia(View view){
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = new Intent(this, MoreNewsActivity.class);
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_INDIA);
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
        intent.putExtra(Constants.EXTRA_MORE_NEWS_TYPE, Constants.KEY_HOLLYWOOD);
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
