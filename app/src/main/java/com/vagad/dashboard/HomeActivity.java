package com.vagad.dashboard;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.VagadApp;
import com.vagad.dashboard.fragments.AboutUsFragment;
import com.vagad.dashboard.fragments.FavListFragment;
import com.vagad.dashboard.fragments.NewsListFragment;
import com.vagad.utils.AlarmUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.NotificationUtils;
import com.vagad.utils.rating.RateItDialogFragment;

public class HomeActivity extends BaseActivity {

    private ViewPager viewPager;
    public NewsListFragment newsListFragment;
    public FavListFragment favListFragment;
    private AboutUsFragment aboutUsFragment;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private boolean mFullAddDisplayed;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_home);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adView = (AdView) findViewById(R.id.adView);
        setPagerAdapter();
        initAds();

        Bundle bundle = new Bundle();
        bundle.putString("Devices", "Mobile Used "+android.os.Build.MODEL);
        VagadApp.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        /**
         * Showing Rating Dialog
         */
        RateItDialogFragment.show(this, getSupportFragmentManager());
    }

    private void initAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        /*AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("FD6CED2CA6E0957AC9A94C05C3FCCD6F")
                .build();*/
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

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setPagerAdapter(){
        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        newsListFragment = new NewsListFragment();
                        return newsListFragment;
                    case 1:
                        favListFragment = new FavListFragment();
                        return favListFragment;
                    case 2:
                        aboutUsFragment = new AboutUsFragment();
                        return aboutUsFragment;
                }
                return null;
            }
            @Override
            public Parcelable saveState() {return null;}
            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }
        };
        viewPager.setAdapter(adapter);
        //viewPager.setPageMargin(20);//Only for showing divider
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
    }


    public void onClickFavourite() {
        viewPager.setCurrentItem(1);
    }

    public void onClickAboutUs() {
        viewPager.setCurrentItem(2);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()){
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(1);
                break;
            default:
                if (mInterstitialAd.isLoaded() && !mFullAddDisplayed) {
                    mInterstitialAd.show();
                    mFullAddDisplayed = true;
                    break;
                }else if(mFullAddDisplayed) {
                    super.onBackPressed();
                    break;
                }else{
                    super.onBackPressed();
                    break;
                }
        }

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
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
