package com.vagad.dashboard.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.dashboard.FavListActivity;
import com.vagad.model.RSSItem;
import com.vagad.utils.DateUtils;

import java.util.List;

public class FavNewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<RSSItem> mNewsList;
    private Context context;
    private FavListActivity favListActivity;

    public FavNewsRecyclerAdapter(List<RSSItem> mNewsList, FavListActivity context, FavListActivity favListActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.favListActivity = favListActivity;
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
            ((VHItem) holder).txtTitle.setText(getItem(position).getTitle());
            ((VHItem) holder).txtDescription.setText(getItem(position).getDescription());
            ((VHItem) holder).txtTime.setText(DateUtils.convertData(getItem(position).getPubdate()));
            Glide.with(context).load(getItem(position).getImage()).placeholder(R.drawable.ic_placeholder).into(((VHItem) holder).imgNews);
            ((VHItem) holder).mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favListActivity.setOnItemClick(getItem(position), ((VHItem) holder).imgNews);
                }
            });
        } else if (holder instanceof VHHeader) {
            if(mNewsList.size() == 0) {
                ((VHHeader) holder).txtNoData.setVisibility(View.VISIBLE);
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
        public CardView mCardView;
        public TextView txtTitle, txtTime, txtDescription;
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
        public VHHeader(View itemView) {
            super(itemView);
            imgCover = (ImageView) itemView.findViewById(R.id.imgCover);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtNoData = (TextView) itemView.findViewById(R.id.txtNodata);
        }
    }


}