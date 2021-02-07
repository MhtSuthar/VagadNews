package com.vagad.busroute.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.base.VagadApp;
import com.vagad.busroute.SearchCityActivity;
import com.vagad.busroute.adapter.BusRouteRecyclerAdapter;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.model.BusListModel;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by Admin on 02-Jul-17.
 */

public class BusRouteSearchFragment extends BaseFragment {

    public static final String TAG = "BusRouteSearchFragment";
    private List<BusListModel> mBusList = new ArrayList<>();
    private BusRouteRecyclerAdapter busRouteRecyclerAdapter;
    private ProgressBar progressBar;
    private List<BusListModel> mTempBusList;

    RecyclerView mRecyclerView;
    Toolbar toolbar;
    private TextView mTextSource, mTextDestination;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_route_search, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint: "+isVisibleToUser);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AnimationUtils.runEnterAnimation(view, getScreenHeight());
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //((MainActivity)getActivity()).setSupportActionBar(toolbar);
         //getActivity().setTitle("");
        setAdapter();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_reload:
                        setAdapter();
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.bus_menu);

        setupSearchView();
    }

    private void setupSearchView() {
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(toolbar.getMenu().findItem(R.id.menu_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.isEmpty(newText)){
                        searchBus("");
                    }else{
                        searchBus(newText);
                    }
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.e("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
    }

    void setAdapter(){
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /**
         * Set Header
         */
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.layout_header_search_bus, mRecyclerView, false);

        progressBar = (ProgressBar) header.findViewById(R.id.progressBar);
        mTextSource = (TextView) header.findViewById(R.id.txt_source);
        mTextSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchCity(Constants.REQUEST_SEARCH_SOURCE);
            }
        });

        header.findViewById(R.id.vagadNews).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVagadNews();
            }
        });

        AppCompatButton mBtnSearch = (AppCompatButton) header.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mTextSource.getText().toString()) && !TextUtils.isEmpty(mTextDestination.getText().toString())){
                    searchBus(mTextSource.getText().toString()+"-"+mTextDestination.getText().toString());
                }else{

                }
            }
        });

        mTextDestination = (TextView) header.findViewById(R.id.txt_destination);
        mTextDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchCity(Constants.REQUEST_SEARCH_DESTINATION);
            }
        });

        mBusList = ((VagadApp) getActivity().getApplication()).getListBus();
        Log.e(TAG, "setAdapter: "+mBusList.size());

        mTempBusList = new ArrayList<>();
        mTempBusList.addAll(mBusList);

        busRouteRecyclerAdapter = new BusRouteRecyclerAdapter(header, getActivity(), mTempBusList, BusRouteSearchFragment.this);
        mRecyclerView.setAdapter(busRouteRecyclerAdapter);
    }

    private void openVagadNews() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my Vagad News App at: https://play.google.com/store/apps/details?id=com.vagad");
        startActivity(sendIntent);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my Bus Route App at: https://play.google.com/store/apps/details?id=com.vagad.busroute");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    void openSearchCity(int requestSearch){
        try {
            Intent intent = new Intent(getActivity(), SearchCityActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), mTextSource, "source");
            startActivityForResult(intent, requestSearch, options.toBundle());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_SEARCH_SOURCE){
                mTextSource.setText(data.getStringExtra(Constants.EXTRA_CITY_NAME));
            }else if(requestCode == Constants.REQUEST_SEARCH_DESTINATION){
                mTextDestination.setText(data.getStringExtra(Constants.EXTRA_CITY_NAME));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void searchBus(String bus) {
        Log.e(TAG, "searchBus: "+bus);
        if(TextUtils.isEmpty(bus)){
            if(busRouteRecyclerAdapter != null){
                busRouteRecyclerAdapter.setSearchString(bus);
                busRouteRecyclerAdapter.setSearchList(mBusList);
            }
        }else{
            if(busRouteRecyclerAdapter != null){
                List<BusListModel> mListFilterContact = new ArrayList<>();
                for (int i = 0; i < mBusList.size(); i++) {
                    if(mBusList.get(i).NAME_OF_ROUTE.toLowerCase().contains(bus.toLowerCase())){
                        mListFilterContact.add(mBusList.get(i));
                    }
                }
                busRouteRecyclerAdapter.setSearchString(bus);
                busRouteRecyclerAdapter.setSearchList(mListFilterContact);
                if(mListFilterContact.size() == 0){

                }
            }
        }
    }
}
