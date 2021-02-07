package com.vagad.dashboard.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.dashboard.fragments.MoreNewsListFragment;
import com.vagad.model.RSSItem;
import com.vagad.utils.AppUtils;
import com.vagad.utils.DateUtils;

import java.util.List;

public class MoreNewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<RSSItem> mNewsList;
    private Context context;
    private MoreNewsListFragment moreNewsListFragment;

    public MoreNewsRecyclerAdapter(List<RSSItem> mNewsList, Context context, MoreNewsListFragment moreNewsListFragment) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.moreNewsListFragment = moreNewsListFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_fav, parent, false);
            return new VHItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_header_fav, parent, false);
            return new VHHeader(v);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VHItem) {
            ((VHItem) holder).txtTitle.setText(AppUtils.fromHtml(getItem(position).getTitle()));
            ((VHItem) holder).txtDescription.setText(AppUtils.fromHtml(getItem(position).getDescription()));
            if(moreNewsListFragment.mNewsType.equals("Latest News")){
                ((VHItem) holder).txtTime.setText(DateUtils.convertData(getItem(position).getPubdate()));
            }else
                ((VHItem) holder).txtTime.setText(getItem(position).getPubdate());
            Glide.with(context).load(getItem(position).getImage()).placeholder(R.drawable.ic_placeholder).into(((VHItem) holder).imgNews);
            ((VHItem) holder).mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreNewsListFragment.setOnItemClick(position, ((VHItem) holder).imgNews);
                }
            });
        } else if (holder instanceof VHHeader) {
            ((VHHeader) holder).txtTitle.setText(moreNewsListFragment.mNewsType);
            /*switch (moreNewsListFragment.mNewsType){

            }*/
            if(mNewsList.size() == 0) {
                ((VHHeader) holder).mProgressBar.setVisibility(View.VISIBLE);
            }else{
                ((VHHeader) holder).mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private RSSItem getItem(int position) {
        return mNewsList.get(position - 1);
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
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtNoData;
        ProgressBar mProgressBar;
        public VHHeader(View itemView) {
            super(itemView);
            imgCover = (ImageView) itemView.findViewById(R.id.imgCover);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtNoData = (TextView) itemView.findViewById(R.id.txtNodata);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }




}