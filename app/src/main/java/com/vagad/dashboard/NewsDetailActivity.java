package com.vagad.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.fragments.HeaderNewsFragment;
import com.vagad.dashboard.fragments.NewsDetailFragment;
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;
import com.vagad.utils.customviews.CustomViewPager;
import com.vagad.utils.pageindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class NewsDetailActivity extends BaseActivity {

    /*private TextView txtTitle, txtTime, txtDesc;
    private ImageView imgCover;*/
    private List<RSSItem> mNewsList = new ArrayList<>();
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    //private FloatingActionButton mBtnFav;
    public RSSDatabaseHandler rssDatabaseHandler;
    public boolean isFavChange;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private CustomViewPager customViewPager;
    private int mWhichPage = 0;
    private static final String TAG = "NewsDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_news_detail_pager);
        mNewsList = getIntent().getParcelableArrayListExtra(Constants.Bundle_Feed_Item);
        mWhichPage = getIntent().getIntExtra(Constants.Bundle_Which_Page, 0);
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        initView();
        setData();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setViewPagerAdapter(ViewPager viewPager){
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mNewsList.size();
            }
            @Override
            public Fragment getItem(int position) {
                NewsDetailFragment headerNewsFragment = new NewsDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.Bundle_Feed_Item, mNewsList.get(position));
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
        viewPager.setOffscreenPageLimit(mNewsList.size());
        viewPager.setCurrentItem(mWhichPage);
    }

    private void setData() {
        setViewPagerAdapter(customViewPager);
    }

    private void initView() {
        customViewPager = (CustomViewPager) findViewById(R.id.view_pager);
    }


    @Override
    public void onBackPressed() {
        if(isFavChange){
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constants.Bundle_Feed_List, (ArrayList<? extends Parcelable>) mNewsList);
            setResult(Activity.RESULT_OK, intent);
            supportFinishAfterTransition();
        }else {
            super.onBackPressed();
            supportFinishAfterTransition();
        }
    }

    public void notifyChange() {
        if(mHeaderPagerAdapter != null){
            mHeaderPagerAdapter.notifyDataSetChanged();
        }
    }
}
