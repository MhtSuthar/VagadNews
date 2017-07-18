package com.vagad.dashboard.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vagad.R;
import com.vagad.busroute.adapter.BusRouteRecyclerAdapter;
import com.vagad.dashboard.NewsListActivity;
import com.vagad.dashboard.fragments.NewsListFragment;
import com.vagad.model.RSSItem;
import com.vagad.utils.AppUtils;
import com.vagad.utils.DateUtils;

import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RSSItem> mNewsList;
    private Context context;
    private NewsListFragment newsListActivity;
    private static final String TAG = "NewsRecyclerAdapter";
    private static final int AD_TYPE = 0;
    private static final int CONTENT_TYPE = 1;
    private boolean isInternetConnected;
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private View header;

    public NewsRecyclerAdapter(View header, List<RSSItem> mNewsList, Context context, NewsListFragment newsListActivity) {
        this.mNewsList = mNewsList;
        this.header = header;
        this.context = context;
        this.newsListActivity = newsListActivity;
        isInternetConnected = AppUtils.isOnline(context);
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(View rowView) {
            super(rowView);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new NewsRecyclerAdapter.ViewHolderHeader(header);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_news, parent, false);
        return new NewsRecyclerAdapter.VHItem(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }
        if (holder instanceof NewsRecyclerAdapter.VHItem) {
            ((VHItem) holder).txtTitle.setText(getItem(position).getTitle());
            ((VHItem) holder).txtDescription.setText(getItem(position).getDescription());
            ((VHItem) holder).txtTime.setText(DateUtils.convertData(getItem(position).getPubdate()));
            Glide.with(context).load(getItem(position).getImage()).placeholder(R.drawable.ic_placeholder).centerCrop().into(((VHItem) holder).imgNews);
            ((VHItem) holder).mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newsListActivity.openNewsDetail(getItem(position), ((VHItem) holder).imgNews, position);
                }
            });
        }
    }

   /* @Override
    public int getItemViewType(int position) {
        if (position % 10 == 0 && position != 0)
            return AD_TYPE;
        return CONTENT_TYPE;
    }*/

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size()  + 1;
    }

    private RSSItem getItem(int position) {
        return mNewsList.get(position-1);
    }

    class VHItem extends RecyclerView.ViewHolder {
        public ImageView imgNews;
        public TextView txtTitle, txtTime, txtDescription;
        public CardView mCardView;
        public VHItem(View itemView) {
            super(itemView);
            imgNews = (ImageView) itemView.findViewById(R.id.imgNews);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

}