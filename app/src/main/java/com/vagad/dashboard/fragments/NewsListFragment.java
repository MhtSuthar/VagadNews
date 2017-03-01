package com.vagad.dashboard.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.HomeActivity;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.adapter.NewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.loder.CircleProgressBar;
import com.vagad.utils.pageindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mohit on 15-Feb-17.
 */

public class NewsListFragment extends BaseFragment {

    private NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private CircleProgressBar progressBar;
    private RSSParser rssParser = new RSSParser();
    private static final String TAG = "NewsListFragment";
    private  RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CirclePageIndicator pageIndicator;
    private Handler handler = new Handler();
    private int delay = 5000;
    private int mVisiblePage = -1;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private ImageView imgNoData;
    private int mStartLatestNews = 0, mEndLatestNews = 5;
    private List<RSSItem> mLatestNewsList = new ArrayList<>();
    private List<RSSItem> mLatestNewsListVisible = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setAllNews();
        if(isOnline(getContext()))
            new LoadRSSFeed().execute();
    }

    public void setAllNews() {
        mNewsList = rssDatabaseHandler.getAllSites();
        mLatestNewsList = rssDatabaseHandler.getLatestNews();
        if(mNewsList.size() > 0){
            imgNoData.setVisibility(View.GONE);
            setRecyclerAdapter();
            setViewPagerAdapter(viewPager);
        }else{
            imgNoData.setVisibility(View.VISIBLE);
        }
    }

    public void setAllNewsForHeaderFavChanges() {
        mNewsList = rssDatabaseHandler.getAllSites();
        if(mNewsList.size() > 0){
            imgNoData.setVisibility(View.GONE);
            setRecyclerAdapter();
            setViewPagerAdapter(viewPager);
        }
        if(((HomeActivity)getActivity()).favListFragment != null){
            ((HomeActivity)getActivity()).favListFragment.setAdapter();
        }
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.pageIndicator);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        rssDatabaseHandler = new RSSDatabaseHandler(getContext());
        imgNoData = (ImageView) view.findViewById(R.id.imgNoData);


        toolbar.inflateMenu(R.menu.home_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_favourite:
                        ((HomeActivity)getActivity()).onClickFavourite();
                        break;
                    case R.id.menu_about_us:
                        ((HomeActivity)getActivity()).onClickAboutUs();
                        break;
                    case R.id.menu_setting:

                        break;
                }
                return true;
            }
        });

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

    public void openNewsDetail(RSSItem rssItem, ImageView imageView, int position) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        //intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
        startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: "+resultCode+"  "+requestCode);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_CODE_NEWS_DETAIL){
               changeInFav(data);
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
        if(((HomeActivity)getActivity()).favListFragment != null){
            ((HomeActivity)getActivity()).favListFragment.setAdapter();
        }

       /* mNewsList = data.getParcelableArrayListExtra(Constants.Bundle_Feed_List);
        newsRecyclerAdapter.notifyDataSetChanged();
        setViewPagerAdapter(viewPager);
        if(((HomeActivity)getActivity()).favListFragment != null){
            ((HomeActivity)getActivity()).favListFragment.setAdapter();
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerAdapter = new NewsRecyclerAdapter(mNewsList, getActivity(), NewsListFragment.this);
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
        mHeaderPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
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
            //showProgress(true);
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
           // showProgress(false);
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
}
