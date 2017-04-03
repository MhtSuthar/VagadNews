package com.vagad.dashboard.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.dashboard.fragments.NewsListFragment;
import com.vagad.model.RSSItem;
import com.vagad.utils.DateUtils;

import org.jsoup.helper.DataUtil;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RSSItem> mNewsList;
    private Context context;
    private NewsListActivity newsListActivity;
    private NewsListFragment newsListFragment;
    private static final String TAG = "NewsRecyclerAdapter";

    public NewsRecyclerAdapter(List<RSSItem> data, Context context, NewsListFragment newsListFragment) {
        this.mNewsList = data;
        this.context = context;
        this.newsListFragment = newsListFragment;
    }

    public NewsRecyclerAdapter(List<RSSItem> mNewsList, NewsListActivity context, NewsListActivity newsListActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.newsListActivity = newsListActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_news, parent, false);
        return new VHItem(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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

    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    private RSSItem getItem(int position) {
        return mNewsList.get(position);
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
}