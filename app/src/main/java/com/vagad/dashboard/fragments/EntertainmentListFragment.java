package com.vagad.dashboard.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Feb-17.
 */

public class EntertainmentListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private FavNewsRecyclerAdapter favNewsRecyclerAdapter;
    private RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private static final String TAG = "FavListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rssDatabaseHandler = new RSSDatabaseHandler(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        setAdapter();
    }

    public void setAdapter() {
        mNewsList = rssDatabaseHandler.getFavList();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       // favNewsRecyclerAdapter = new FavNewsRecyclerAdapter(mNewsList, getActivity(), EntertainmentListFragment.this);
        recyclerView.setAdapter(favNewsRecyclerAdapter);
    }

    public void setOnItemClick(int position, ImageView imageView) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Which_Page, position-1);
        intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imageView, "profile");
        startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_CODE_NEWS_DETAIL){
                setAdapter();
            }
        }
    }

}
