package com.vagad.dashboard.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.base.BaseFragment;
import com.vagad.dashboard.NewsDetailActivity;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;

/**
 * Created by Admin on 15-Feb-17.
 */

public class NewsDetailFragment extends BaseFragment {

    private RSSItem rssItem;
    private ImageView imgCover, imgBack, imgShare, imgFav;
    private TextView txtTitle;
    private TextView txtDesc;
    private TextView txtTime;
    private RelativeLayout relHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rssItem = (RSSItem) getArguments().getParcelable(Constants.Bundle_Feed_Item);
        initView(view);
        setData();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private void setData() {
        Glide.with(getActivity()).load(rssItem.getImage()).placeholder(R.drawable.ic_placeholder).into(imgCover);
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

    private void initView(View view) {
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        imgCover = (ImageView) view.findViewById(R.id.imgCover);
        txtDesc = (TextView) view.findViewById(R.id.txtDescription);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        relHeader = (RelativeLayout) view.findViewById(R.id.relHeader);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);
        imgShare = (ImageView) view.findViewById(R.id.imgShare);
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            relHeader.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsDetailActivity)getActivity()).onBackPressed();
            }
        });

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsDetailActivity)getActivity()).isFavChange = true;
                if(v.getTag().toString().equals("0")){
                    v.setTag("1");
                    rssItem.setFav(true);
                    imgFav.setImageResource(R.drawable.ic_fav_select);
                    ((NewsDetailActivity)getActivity()).rssDatabaseHandler.setFav(1, ""+rssItem.getId());
                }else{
                    rssItem.setFav(false);
                    v.setTag("0");
                    ((NewsDetailActivity)getActivity()).rssDatabaseHandler.setFav(0, ""+rssItem.getId());
                    imgFav.setImageResource(R.drawable.ic_tab_fav);
                }
                ((NewsDetailActivity)getActivity()).notifyChange();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Bundle_Feed_Item, rssItem);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            rssItem = savedInstanceState.getParcelable(Constants.Bundle_Feed_Item);
        }
    }

    private void openShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ""+rssItem.getTitle()+"\n"+rssItem.getDescription()+" Thanks For Using Vagad News. Please download from play store https://play.google.com/store/apps/details?id=com.vagad");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void openNewsDetail(RSSItem rssItem) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(Constants.Bundle_Feed_Item, rssItem);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), imgCover, "profile");
        startActivity(intent, options.toBundle());
    }
}
