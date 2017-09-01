package com.vagad.dashboard.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.adapter.MoreNewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.rest.RSSParser;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 15-Feb-17.
 */

public class MoreNewsListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private MoreNewsRecyclerAdapter moreNewsRecyclerAdapter;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private static final String TAG = "MoreNewsListFragment";
    public String mNewsType = "Latest News";
    private List<RSSItem> mLatestNews;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AnimationUtils.runEnterAnimation(view, getScreenHeight());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        if(mLatestNews != null && mLatestNews.size() > 0){
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            moreNewsRecyclerAdapter = new MoreNewsRecyclerAdapter(mLatestNews, getActivity(), MoreNewsListFragment.this);
            recyclerView.setAdapter(moreNewsRecyclerAdapter);
            return;
        }
        mNewsType = getArguments().getString(Constants.EXTRA_MORE_NEWS_TYPE);
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
        intent.putExtra(Constants.Bundle_Is_From_More_News, true);
        if(mLatestNews != null && mLatestNews.size() > 0)
            intent.putExtra(Constants.Bundle_Feed_Item, mLatestNews.get(position-1));
        else
            intent.putExtra(Constants.Bundle_Feed_Item, mNewsList.get(position-1));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    public void setLatestNews(ArrayList<RSSItem> latestNews) {
        this.mLatestNews = latestNews;
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
                        //VIDEO: करते हैं जब किसी पे करम...., <img src='http://img01.ibnlive.in/ibnkhabar/uploads/2017/07/habib-1.png' height='50' width='76' />उर्दू भाषा की काव्य गोष्ठी को मुशायरा कहते हैं. मुशायरा शब्द हिन्दी में उर्दू से आया है और यह उस महफ़िल की व्याख्या करता है जिसमें अनेक जगहों से शायर शिरकत कर अपना अपना काव्य पाठ करते हैं.मुशायरा उत्तर भारत और पाकिस्तान की संस्कृति का अभिन्न अंग है और इसे प्रतिभागियों द्वारा मुक्त आत्म अभिव्यक्ति के एक माध्यम के रूप में सराहा जाता है.न्यूज 18 हिंदी उर्दू और उर्दू शायरी से प्यार करने वालों लिए लाया है दुबई में आयोजित हुआ मुशायरा जश्न-ए-जम्हूरियत-ए-हिंद यानि जश्न-ए-हिंदुस्तान.इस मुशायरे की इस कड़ी में शायर शेख हबीब पेश कर रहे  हैं अपने कुछ शेर.सुनिए और आनंद लीजिए -,
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_entertainment));
                        break;
                    case Constants.KEY_ASTROLOGY:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_astro));
                        break;
                    case Constants.KEY_EDUCATION:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_career));
                        break;
                    case Constants.KEY_SPORT:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_sport));
                        break;
                    case Constants.KEY_HEALTH:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_helth));
                        break;
                    case Constants.KEY_POLITICS:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_politics));
                        break;
                    case Constants.KEY_HOLLYWOOD:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_hollywood));
                        break;
                    case Constants.KEY_WORD:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_world));
                        break;
                    case Constants.KEY_FILM_REVIEW:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_film_review));
                        break;
                    case Constants.KEY_GADGET:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_gadget));
                        break;
                    case Constants.KEY_RAJASTHAN:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_rajasthan));
                        break;
                    case Constants.KEY_INDIA:
                        rssFeed = rssParser.getRSSFeedItems(getString(R.string.feed_news18_india));
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
                for (int i = 0; i < args.size(); i++) {
                    String imgRegex = "<[iI][mM][gG][^>]+[sS][rR][cC]\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";

                    Pattern p = Pattern.compile(imgRegex);
                    Matcher m = p.matcher(args.get(i).getDescription());

                    if (m.find()) {
                        try {
                            String imgSrc = m.group(1);
                            //Log.e(TAG, "desc  "+args.get(i).getDescription());
                            args.get(i).setImage(imgSrc);
                            if(args.get(i).getDescription().contains("/>"))
                                args.get(i).setDescription(args.get(i).getDescription().split("/>")[1]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
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
