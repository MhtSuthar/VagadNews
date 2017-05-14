package com.vagad.dashboard.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.vagad.R;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.model.RSSItem;
import com.vagad.utils.DateUtils;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RSSItem> mNewsList;
    private Context context;
    private NewsListActivity newsListActivity;
    private static final String TAG = "NewsRecyclerAdapter";
    private static final int AD_TYPE = 0;
    private static final int CONTENT_TYPE = 1;

    public NewsRecyclerAdapter(List<RSSItem> mNewsList, NewsListActivity context, NewsListActivity newsListActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.newsListActivity = newsListActivity;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTENT_TYPE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_news, parent, false);
            return new NewsRecyclerAdapter.VHItem(v);
        } else if (viewType == AD_TYPE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_ads, parent, false);
            return new NewsRecyclerAdapter.VHAds(v);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsRecyclerAdapter.VHItem) {
            ((VHItem) holder).txtTitle.setText(getItem(position).getTitle());
            ((VHItem) holder).txtDescription.setText(getItem(position).getDescription());
            ((VHItem) holder).txtTime.setText(DateUtils.convertData(getItem(position).getPubdate()));
            Glide.with(context).load(getItem(position).getImage()).placeholder(R.drawable.ic_placeholder).centerCrop().into(((VHItem) holder).imgNews);
            ((VHItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsListActivity.openNewsDetail(getItem(position), ((VHItem) holder).imgNews, position);
                }
            });
        }else if (holder instanceof NewsRecyclerAdapter.VHAds) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 10 == 0 && position != 0)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    int actualPos(int pos){
        pos = pos -  (pos / 10);
        return pos;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size() + ( mNewsList.size() / 10);
    }

    private RSSItem getItem(int position) {
        return mNewsList.get(actualPos(position));
    }

    class VHItem extends RecyclerView.ViewHolder {
        public ImageView imgNews;
        public TextView txtTitle, txtTime, txtDescription;
        public VHItem(View itemView) {
            super(itemView);
            imgNews = (ImageView) itemView.findViewById(R.id.imgNews);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        }
    }

    class VHAds extends RecyclerView.ViewHolder {
        public AdView adView;
        public VHAds(View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
        }
    }
}