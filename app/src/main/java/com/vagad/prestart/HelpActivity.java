package com.vagad.prestart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.model.TokenModel;
import com.vagad.prestart.fragment.HelpPagerFragment;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.Constants;
import com.vagad.utils.pageindicator.CirclePageIndicator;


/**
 * Created by ubuntu on 15/9/16.
 */
public class HelpActivity extends BaseActivity {

    public final static String Position = "pos";
    private ViewPager mViewpager;
    private CirclePageIndicator mIndicator;
    private RelativeLayout relHeader;

    private static final String TAG = "HelpActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        init();

        setPagerAdapter();

        /*if(isOnline(this))
            getAndCheckToken();*/
    }



    public void onClickDone(View view){
        SharedPreferenceUtil.putValue(Constants.KEY_HELP_SCREEN_APPEAR, true);
        SharedPreferenceUtil.save();
        Intent intent = new Intent(HelpActivity.this, NewsListActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        relHeader = (RelativeLayout) findViewById(R.id.relHeader);
        mViewpager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            relHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }


    private void setPagerAdapter(){
        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 4;
            }
            @Override
            public Fragment getItem(int position) {
                HelpPagerFragment fragment = new HelpPagerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Position, position);
                fragment.setArguments(bundle);
                return fragment;
            }
            @Override
            public Parcelable saveState() {return null;}
        };
        mViewpager.setAdapter(adapter);
        mIndicator.setViewPager(mViewpager);
        mViewpager.setCurrentItem(0);
    }
}
