package com.vagad.dashboard.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.adapter.FavNewsRecyclerAdapter;
import com.vagad.dashboard.adapter.MoreNewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.AnimationUtils;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Feb-17.
 */

public class MoreNewsListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private MoreNewsRecyclerAdapter moreNewsRecyclerAdapter;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private static final String TAG = "MoreNewsListFragment";
    public String mNewsType = Constants.KEY_ENTERTAINMENT;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AnimationUtils.runEnterAnimation(view, getScreenHeight());
        mNewsType = getArguments().getString(Constants.EXTRA_MORE_NEWS_TYPE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        setAdapter();
        if(AppUtils.isOnline(getActivity())){
            new LoadRSSFeed().execute();
        }
    }

    public void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moreNewsRecyclerAdapter = new MoreNewsRecyclerAdapter(mNewsList, getActivity(), MoreNewsListFragment.this);
        recyclerView.setAdapter(moreNewsRecyclerAdapter);
    }

    public void setOnItemClick(int position, ImageView imageView) {
        /*Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Which_Page, position-1);
        intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
        startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());*/


        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Is_From_Local_News, true);
        intent.putExtra(Constants.Bundle_Feed_Item, mNewsList.get(position-1));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    /**
     * Background Async Task to get RSS data from URL
     * */
    class LoadRSSFeed extends AsyncTask<String, String, List<RSSItem>> {

        private RSSParser rssParser = new RSSParser();
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mProgressBarToolbar.setVisibility(View.VISIBLE);
        }

        /**
         * getting Inbox JSON
         * */
        @Override
        protected List<RSSItem> doInBackground(String... args) {
            try {
                List<RSSItem> rssFeed = new ArrayList<>();
                switch (mNewsType){
                    case Constants.KEY_ENTERTAINMENT:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_entertainment));
                        break;
                    case Constants.KEY_ASTROLOGY:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_astrology));
                        break;
                    case Constants.KEY_EDUCATION:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_education));
                        break;
                    case Constants.KEY_SPORT:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_sport));
                        break;
                    case Constants.KEY_HEALTH:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_helth));
                        break;
                    case Constants.KEY_POLITICS:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_politics));
                        break;
                    case Constants.KEY_BOLLYWOOD:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_bollywood));
                        break;
                    case Constants.KEY_WORD:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_url_world));
                        break;
                }

                return rssFeed;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(List<RSSItem> args) {
            //mProgressBarToolbar.setVisibility(View.GONE);
            if(args != null) {
                mNewsList.clear();
                mNewsList.addAll(args);
                if (moreNewsRecyclerAdapter == null) {
                    setAdapter();
                } else {
                    moreNewsRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }

    }


}
