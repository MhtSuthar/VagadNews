package com.vagad.localnews.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.BuildConfig;
import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.fcm.FcmUtils;
import com.vagad.localnews.AddNewsActivity;
import com.vagad.localnews.ReporterNewsListActivity;
import com.vagad.localnews.adapter.ReportNewsRecyclerAdapter;
import com.vagad.model.NewsPostModel;
import com.vagad.model.RSSItem;
import com.vagad.utils.AppUtils;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Admin on 15-Feb-17.
 */

public class ReporterNewsListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private ReportNewsRecyclerAdapter mReportNewsRecyclerAdapter;
    private static final String TAG = "ReporterNewsListFragment";
    private List<NewsPostModel> mListNews = new ArrayList<>();
    private boolean isDeleteHappen;
    private ProgressBar progressBar;
    private FloatingActionButton mFabAdd;
    private Toolbar toolbar;
    private RelativeLayout mRelNoData;
    private DatabaseReference mDatabase;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mFabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mRelNoData = (RelativeLayout) view.findViewById(R.id.relNoData);
        mFabAdd.setVisibility(View.GONE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_add:
                        Intent intent = new Intent(getActivity(), AddNewsActivity.class);
                        startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_ADD);
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.event_menu);
        setAdapter();
        getValFromFirebase();

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewsActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((NewsListActivity)getActivity()).hideBottomnavigation();
                } else {
                    ((NewsListActivity)getActivity()).showBottomnavigation();
                }
            }
        });
    }


    private void getValFromFirebase() {
        if(isOnline(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);
            mDatabase.addValueEventListener(valueEventListener);
        }else{
            showSnackbar(mRootView, getString(R.string.no_internet));
            progressBar.setVisibility(View.GONE);
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    NewsPostModel changedPost = messageSnapshot.getValue(NewsPostModel.class);
                    //changedPost.key = dataSnapshot.getKey();
                    Log.e(TAG, "for : " + changedPost.nameReporter + " key   " + changedPost.key + "   " + changedPost.newsTitle);
                    if (changedPost.isVisible)
                        mListNews.add(changedPost);
                }
                Log.e(TAG, "mListNews Size "+mListNews.size());
                mListNews = removeDuplicates(mListNews);
                Collections.reverse(mListNews);
                progressBar.setVisibility(View.GONE);
                setAdapter();
                mDatabase.removeEventListener(this);
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
            progressBar.setVisibility(View.GONE);
        }
    };

    public <T> void removeDuplicates1(List<T> list) {
        int size = list.size();
        int out = 0;
        {
            final Set<T> encountered = new HashSet<T>();
            for (int in = 0; in < size; in++) {
                final T t = list.get(in);
                final boolean first = encountered.add(t);
                if (first) {
                    list.set(out++, t);
                }
            }
        }
        while (out < size) {
            list.remove(--size);
        }
    }

    public ArrayList<NewsPostModel> removeDuplicates(List<NewsPostModel> list){
        Set set = new TreeSet(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if(((NewsPostModel)o1).newsTitle.equalsIgnoreCase(((NewsPostModel)o2).newsTitle)){
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);

        final ArrayList newList = new ArrayList(set);
        return newList;
    }

    public void setAdapter() {
        if(mListNews.size() == 0){
            mRelNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            mRelNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReportNewsRecyclerAdapter = new ReportNewsRecyclerAdapter(mListNews, getActivity(), ReporterNewsListFragment.this);
        recyclerView.setAdapter(mReportNewsRecyclerAdapter);
    }


    public void setOnItemClick(int position, ImageView imgNews) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Is_From_News_List, true);
        intent.putExtra(Constants.Bundle_Is_From_Local_News, true);
        Constants.mClickImagePath = mListNews.get(position).image;
        intent.putExtra(Constants.Bundle_Feed_Item, getRssItem(mListNews.get(position)));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imgNews, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else{
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
        }
    }

    private RSSItem getRssItem(NewsPostModel newsPostModel) {
        RSSItem rssItem = new RSSItem(newsPostModel.newsTitle, newsPostModel.nameReporter, newsPostModel.newsDesc, ""+newsPostModel.timestamp, "", "", newsPostModel.mobileNo);
        return rssItem;
    }

    public void onEditReport(NewsPostModel item) {
        Constants.mClickImagePath = item.image;
        item.image = "";
        Intent intent = new Intent(getActivity(), AddNewsActivity.class);
        intent.putExtra(Constants.EXTRA_NEWS, item);
        startActivityForResults(intent, getActivity(), false, Constants.REQUEST_CODE_NEWS_EDIT);
    }

    public void onDeleteReport(final NewsPostModel item, final int position) {
        showAlertDialog(new BaseFragment.OnDialogClick() {
            @Override
            public void onPositiveBtnClick() {
                if(AppUtils.isOnline(getActivity())) {
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_NEWS);
                    mDatabase.child(item.key).removeValue();
                    mListNews.remove(position);
                    mReportNewsRecyclerAdapter.notifyItemRemoved(position);
                }else{
                    showSnackbar(mRootView, getString(R.string.no_internet));
                }
            }

            @Override
            public void onNegativeBtnClick() {

            }
        }, "Delete News!", "Are you sure you want to delete this news?", true);
        //mReportNewsRecyclerAdapter.notifyDataSetChanged();
        //getValFromFirebase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: "+resultCode+"  "+requestCode);
        if(resultCode == Activity.RESULT_OK){
            /*if(requestCode == Constants.REQUEST_CODE_NEWS_EDIT){
                mListNews.clear();
                getValFromFirebase();
            }*/
            mListNews.clear();
            getValFromFirebase();

            /**
             * Send All Device Notification
             */
            if(isOnline(getActivity()) /*&& !BuildConfig.DEBUG*/)
                FcmUtils.getAllDeviceToken();
        }
    }
}
