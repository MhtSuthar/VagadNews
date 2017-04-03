package com.vagad.dashboard;

import android.app.Activity;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ImageView;

import com.vagad.R;
import com.vagad.base.BaseActivity;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.adapter.FavNewsRecyclerAdapter;
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Feb-17.
 */

public class FavListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FavNewsRecyclerAdapter favNewsRecyclerAdapter;
    private RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mNewsList = new ArrayList<>();
    private static final String TAG = "FavListActivity";
    private boolean mIsFavChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.fragment_fav_list);
        rssDatabaseHandler = new RSSDatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setAdapter();
    }

    private void fullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public void setAdapter() {
        mNewsList = rssDatabaseHandler.getFavList();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favNewsRecyclerAdapter = new FavNewsRecyclerAdapter(mNewsList, this, FavListActivity.this);
        recyclerView.setAdapter(favNewsRecyclerAdapter);

    }

    public void setOnItemClick(int position, ImageView imageView) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Which_Page, position-1);
        intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) mNewsList);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageView, "profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
        }else
            startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_CODE_NEWS_DETAIL){
                setAdapter();
                mIsFavChange = true;
                /*if(((HomeActivity)getActivity()).newsListFragment != null){
                    ((HomeActivity)getActivity()).newsListFragment.setAllNews();
                }*/
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mIsFavChange){
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_REFRESH, true);
            setResult(Activity.RESULT_OK, intent);
            supportFinishAfterTransition();
        }else {
            super.onBackPressed();
            supportFinishAfterTransition();
        }
    }
}
