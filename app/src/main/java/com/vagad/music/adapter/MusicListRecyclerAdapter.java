package com.vagad.music.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vagad.R;
import com.vagad.model.MusicModel;
import com.vagad.music.VagadMusicActivity;
import com.vagad.utils.AppUtils;

import java.util.List;

public class MusicListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<MusicModel> mNewsList;
    private Context context;
    private VagadMusicActivity vagadMusicActivity;

    public MusicListRecyclerAdapter(List<MusicModel> mNewsList, Context context, VagadMusicActivity vagadMusicActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.vagadMusicActivity = vagadMusicActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_music_list, parent, false);
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
            ((VHItem) holder).txtTitle.setText(AppUtils.fromHtml(getItem(position).name));
            ((VHItem) holder).txtDescription.setText(AppUtils.fromHtml(getItem(position).description));
             ((VHItem) holder).mLinDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vagadMusicActivity.setOnItemClick(getItem(position));
                }
            });
        } else if (holder instanceof VHHeader) {
            ((VHHeader) holder).txtTitle.setText("Vagad Music");
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

    private MusicModel getItem(int position) {
        return mNewsList.get(position - 1);
    }

    class VHItem extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtDescription;
        public CardView mCardView;
        public LinearLayout mLinDown;
        public VHItem(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            mLinDown = (LinearLayout) itemView.findViewById(R.id.lin_download);
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