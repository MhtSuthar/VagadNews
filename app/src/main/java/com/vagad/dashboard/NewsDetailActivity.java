package com.vagad.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.dashboard.fragments.NewsDetailFragment;
import com.vagad.localnews.adapter.ReportNewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;
import com.vagad.utils.customviews.CustomViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsDetailActivity extends BaseActivity {

    private TextView txtTitle, txtTime, txtDesc, txt_more_read;
    private ImageView imgCover, imgBack, imgFav;
    private FloatingActionButton btnShare;
    private LinearLayout linNewsDetail;
    private RelativeLayout relHeader;
    private List<RSSItem> mNewsList = new ArrayList<>();
    public RSSDatabaseHandler rssDatabaseHandler;
    public boolean isFavChange;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private CustomViewPager customViewPager;
    private int mWhichPage = 0;
    private boolean mIsFromNewsList, mIsFromLocalNews;
    private static final String TAG = "NewsDetailActivity";
    private RSSItem rssItem;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_news_detail_pager);
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        initView();
        setupAds();
        mIsFromNewsList = getIntent().getBooleanExtra(Constants.Bundle_Is_From_News_List, false);
        if(mIsFromNewsList){
            mIsFromLocalNews = getIntent().getBooleanExtra(Constants.Bundle_Is_From_Local_News, false);
            AnimationUtils.animateScaleOut(btnShare);
            rssItem = getIntent().getParcelableExtra(Constants.Bundle_Feed_Item);
            setDataFromNewsList();
        }else {
            linNewsDetail.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
            customViewPager.setVisibility(View.VISIBLE);
            mNewsList = getIntent().getParcelableArrayListExtra(Constants.Bundle_Feed_Item);
            mWhichPage = getIntent().getIntExtra(Constants.Bundle_Which_Page, 0);
            setData();
        }
    }

    private void setupAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        /*AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("FD6CED2CA6E0957AC9A94C05C3FCCD6F")
                .build();*/
        adView.loadAd(adRequest);

        if(!isOnline(this))
            adView.setVisibility(View.GONE);
    }


    private void setDataFromNewsList() {
        if(mIsFromLocalNews){
            imgFav.setVisibility(View.GONE);
            txt_more_read.setVisibility(View.GONE);
            try {
                if(rssItem.getImage().contains(".png") || rssItem.getImage().contains(".jpg")){
                    Glide.with(this).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(imgCover);
                    txtTime.setText(DateUtils.convertData(rssItem.getPubdate()));
                }else {
                    Glide.with(this).load(decodeFromFirebaseBase64(Constants.mClickImagePath)).asBitmap().
                            placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(imgCover);
                    txtTime.setText(DateUtils.getDate(Long.parseLong(rssItem.getPubdate())));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //get_news_type is mobile no & link is reporter name
            txtTitle.setText(rssItem.getTitle()+"\n Created By : "+rssItem.getLink()+" "+rssItem.get_news_type());

            txtDesc.setText(rssItem.getDescription());
        }else {
            Glide.with(this).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(imgCover);
            txtTitle.setText(rssItem.getTitle());
            txtTime.setText(DateUtils.convertData(rssItem.getPubdate()));
            txtDesc.setText(rssItem.getDescription());
            if(rssItem.isFav()){
                imgFav.setTag("1");
                imgFav.setImageResource(R.drawable.ic_fav_select);
            }else{
                imgFav.setTag("0");
                imgFav.setImageResource(R.drawable.ic_tab_fav);
            }
        }

    }

    public static  byte[]  decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return decodedByteArray;
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
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        txtDesc = (TextView) findViewById(R.id.txtDescription);
        txtTime = (TextView) findViewById(R.id.txtTime);
        relHeader = (RelativeLayout) findViewById(R.id.relHeader);
        txt_more_read = (TextView) findViewById(R.id.txt_more_read);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgFav = (ImageView) findViewById(R.id.imgFav);
        btnShare = (FloatingActionButton) findViewById(R.id.btn_share);
        linNewsDetail = (LinearLayout) findViewById(R.id.linNewsDetail);
        adView = (AdView) findViewById(R.id.adView);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            relHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txt_more_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRajasthanLink();
            }
        });

        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreviewImage();
            }
        });

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavChange = true;
                if(v.getTag().toString().equals("0")){
                    v.setTag("1");
                    rssItem.setFav(true);
                    imgFav.setImageResource(R.drawable.ic_fav_select);
                    rssDatabaseHandler.setFav(1, ""+rssItem.getId());
                }else{
                    rssItem.setFav(false);
                    v.setTag("0");
                    rssDatabaseHandler.setFav(0, ""+rssItem.getId());
                    imgFav.setImageResource(R.drawable.ic_tab_fav);
                }
            }
        });
    }

    private void openPreviewImage() {
        Intent intent = new Intent(this, PreviewImageActivity.class);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imgCover, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    private void openShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ""+rssItem.getTitle()+"\n"+rssItem.getDescription()+" Thanks For Using Vagad News. Please download from play store https://play.google.com/store/apps/details?id=com.vagad");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void openRajasthanLink() {
        /*String url = rssItem.getLink();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/
        Intent intent = new Intent(this, OpenUrlActivity.class);
        intent.putExtra(Constants.EXTRA_URL, rssItem.getLink());
        moveActivity(intent, this, false);
    }


    @Override
    public void onBackPressed() {
        if(isFavChange && mIsFromNewsList){
            Intent intent = new Intent();
            intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
            setResult(Activity.RESULT_OK, intent);
            supportFinishAfterTransition();
        }else if(isFavChange){
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
