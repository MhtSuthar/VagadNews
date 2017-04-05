package com.vagad.dashboard.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 15-Feb-17.
 */

public class HeaderNewsFragment extends BaseFragment {

    private RSSItem rssItem;
    private ImageView imgCover;
    private TextView txtTitle;
    private List<RSSItem> list;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_header_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rssItem = (RSSItem) getArguments().getParcelable(Constants.Bundle_Feed_Item);
        initView(view);
        setData();
    }

    private void setData() {
        Glide.with(getActivity()).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(imgCover);
        txtTitle.setText(rssItem.getTitle());
    }

    private void initView(View view) {
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        imgCover = (ImageView) view.findViewById(R.id.imgCover);

        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openNewsDetail(rssItem);
            }
        });
    }

    private void openNewsDetail(RSSItem rssItem) {
        /*Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imgCover, "profile");
        startActivity(intent, options.toBundle());*/

        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Which_Page, getArguments().getInt(Constants.Bundle_Pos));
        intent.putParcelableArrayListExtra(Constants.Bundle_Feed_Item, (ArrayList<? extends Parcelable>) list);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imgCover, "profile");
        startActivityForResult(intent, Constants.REQUEST_CODE_NEWS_DETAIL, options.toBundle());
    }

    public void setList(List<RSSItem> list) {
        if(this.list == null)
            this.list = list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.REQUEST_CODE_NEWS_DETAIL){
                ((NewsListActivity)getActivity()).setAllNewsForHeaderFavChanges();
            }
        }
    }
}
