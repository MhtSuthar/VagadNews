package com.vagad.dashboard.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.base.VagadApp;
import com.vagad.dashboard.AboutUsActivity;
import com.vagad.dashboard.EPaperActivity;
import com.vagad.dashboard.FavListActivity;
import com.vagad.dashboard.MoreNewsActivity;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.dashboard.adapter.NewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.music.VagadMusicActivity;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;
import com.vagad.utils.loder.CircleProgressBar;
import com.vagad.utils.pageindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mohit on 15-Feb-17.
 */

public class NewsListFragment extends BaseFragment{

    private NewsRecyclerAdapter newsRecyclerAdapter;
    private RecyclerView recyclerView;
    private CircleProgressBar progressBar;
    private CirclePageIndicator pageIndicator;
    private RSSParser rssParser = new RSSParser();
    private static final String TAG = "NewsListActivity";
    private  RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private Toolbar toolbar;
    private ProgressBar mProgressBarToolbar;
    private Handler handler = new Handler();
    private int delay = 5000;
    private int mVisiblePage = -1;
    private FragmentStatePagerAdapter mHeaderPagerAdapter;
    private RelativeLayout mRelNoData;
    private int mStartLatestNews = 0, mEndLatestNews = 5;
    private List<RSSItem> mLatestNewsList = new ArrayList<>();
    private List<RSSItem> mLatestNewsListVisible = new ArrayList<>();
    private int[] mImages = new int[]{R.drawable.splash_bg, R.drawable.help_two, R.drawable.help_three, R.drawable.help_four};
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        setAllNews();

        if(isOnline(getActivity()))
            new LoadRSSFeed().execute();

        Bundle bundle = new Bundle();
        bundle.putString("Devices", "Mobile Used "+ Build.MODEL);
        VagadApp.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline(getActivity()))
                    new LoadRSSFeed().execute();
                else
                    mSwipeRefreshLayout.setRefreshing(false);
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
            setRecyclerAdapter();
        }else{
            mRelNoData.setVisibility(View.VISIBLE);
        }
    }


    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        rssDatabaseHandler = new RSSDatabaseHandler(getActivity());
        mRelNoData = (RelativeLayout) view.findViewById(R.id.relNoData);
        mProgressBarToolbar = (ProgressBar) view.findViewById(R.id.progressBarToolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                R.color.colorPrimaryDark,
                android.R.color.holo_orange_light,
                R.color.colorAccent);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_favourite:
                        Intent intent = new Intent(getActivity(), FavListActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivityForResult(intent, Constants.REQUEST_CODE_FAV_NEWS,
                                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                        }else{
                            startActivityForResult(intent, Constants.REQUEST_CODE_FAV_NEWS);
                        }
                        break;
                    case R.id.menu_about_us:
                        moveActivity(new Intent(getActivity(), AboutUsActivity.class), getActivity(), false);
                        break;
                    case R.id.menu_feedback:
                        sendFeedback();
                        break;
                    case R.id.menu_share:
                        shareApp();
                        break;
                    case R.id.menu_more_news:
                        ((NewsListActivity)getActivity()).openMoreNews();
                        break;
                    case R.id.menu_e_paper:
                        Intent mIntent = new Intent(getActivity(), EPaperActivity.class);
                        startActivity(mIntent);
                        break;
                    case R.id.menu_music:
                        startActivity(new Intent(getActivity(), VagadMusicActivity.class));
                        break;
                    case R.id.menu_like:
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                        String facebookUrl = AppUtils.getFacebookPageURL(getActivity());
                        facebookIntent.setData(Uri.parse(facebookUrl));
                        startActivity(facebookIntent);
                        break;
                    case R.id.menu_more_apps:
                        //FcmUtils.sendMultipleDeviceNotification(SharedPreferenceUtil.getString(Constants.FIREBASE_USERS_TOKEN, ""));
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.simplywall")));
                        Uri uri = Uri.parse("market://search?q=pub:Vagad Droid");
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.home_menu);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up hide
                    ((NewsListActivity)getActivity()).hideBottomnavigation();
                } else {
                    // Scrolling down visible
                    ((NewsListActivity)getActivity()).showBottomnavigation();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                    Log.e(TAG, "onScrollStateChanged: SCROLL_STATE_FLING");
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                    Log.e(TAG, "onScrollStateChanged: SCROLL_STATE_TOUCH_SCROLL");
                } else {
                    // Do something
                    Log.e(TAG, "onScrollStateChanged: else");
                }
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


    private void sendFeedback() {
        ShareCompat.IntentBuilder.from(getActivity())
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
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        //intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**
         * Set Header
         */
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_news, recyclerView, false);
        LinearLayout linParent = (LinearLayout) header.findViewById(R.id.lin_parent);


        ViewGroup.LayoutParams params = linParent.getLayoutParams();
            params.height = (int) (SharedPreferenceUtil.getInt(Constants.KEY_SCREEN_HEIGHT, 780) / 2.2);
        linParent.setLayoutParams(params);

        header.findViewById(R.id.txt_see_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreNewsActivity.class);
                intent.putParcelableArrayListExtra(Constants.Bundle_Feed_List, (ArrayList<? extends Parcelable>) mLatestNewsList);
                startActivity(intent);
            }
        });
        viewPager = (ViewPager) header.findViewById(R.id.viewPager);
        pageIndicator = (CirclePageIndicator) header.findViewById(R.id.pageIndicator);
        setViewPagerAdapter(viewPager);

        newsRecyclerAdapter = new NewsRecyclerAdapter(header, mNewsList, getActivity(), NewsListFragment.this);
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

    public void setAllNewsForHeaderFavChanges() {
        mNewsList = rssDatabaseHandler.getAllSites();
        if(mNewsList.size() > 0){
            setRecyclerAdapter();
            setViewPagerAdapter(viewPager);
        }
    }

    public void setViewPagerAdapter(ViewPager viewPager){
        if(getActivity() != null && viewPager != null) {
            final List<RSSItem> mRandomList = getRandomList();
            mHeaderPagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public int getCount() {
                    return mRandomList.size();
                }

                @Override
                public Fragment getItem(int position) {
                    HeaderNewsFragment headerNewsFragment = new HeaderNewsFragment(NewsListFragment.this);
                    headerNewsFragment.setList(mLatestNewsListVisible);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.Bundle_Pos, position);
                    bundle.putParcelable(Constants.Bundle_Feed_Item, mRandomList.get(position));
                    headerNewsFragment.setArguments(bundle);
                    return headerNewsFragment;
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
                    rssFeed.addAll(rssParser.getRSSFeedItems(getString(R.string.feed_news18_rajasthan)));
                    for (int i = 0; i < rssFeed.size(); i++) {
                        String imgRegex = "<[iI][mM][gG][^>]+[sS][rR][cC]\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
                        Pattern p = Pattern.compile(imgRegex);
                        Matcher m = p.matcher(rssFeed.get(i).getDescription());
                        if (m.find()) {
                            try {
                                String imgSrc = m.group(1);
                                //Log.e(TAG, "desc  "+args.get(i).getDescription());
                                rssFeed.get(i).setImage(imgSrc);
                                //TODO Check this index out bound exeption
                                if(rssFeed.get(i).getDescription().contains("/>")
                                        && rssFeed.get(i).get_news_type().equals(Constants.NEWS_TYPE_LATEST)
                                        && rssFeed.get(i).getDescription().split("/>").length > 1)
                                    rssFeed.get(i).setDescription(rssFeed.get(i).getDescription().split("/>")[1]);
                            }catch (Exception e){
                                e.printStackTrace();
                                continue;
                            }
                        }
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
            mSwipeRefreshLayout.setRefreshing(false);
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


}
