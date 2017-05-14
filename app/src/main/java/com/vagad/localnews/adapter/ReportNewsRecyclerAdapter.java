package com.vagad.localnews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.dashboard.FavListActivity;
import com.vagad.localnews.ReporterNewsListActivity;
import com.vagad.model.NewsPostModel;
import com.vagad.model.RSSItem;
import com.vagad.utils.DateUtils;

import java.io.IOException;
import java.util.List;

public class ReportNewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<NewsPostModel> mNewsList;
    private Context context;
    private ReporterNewsListActivity favListActivity;

    public ReportNewsRecyclerAdapter(List<NewsPostModel> mNewsList, ReporterNewsListActivity context, ReporterNewsListActivity favListActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.favListActivity = favListActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_report, parent, false);
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
            ((VHItem) holder).txtTitle.setText(getItem(position).newsTitle);
            ((VHItem) holder).txtDescription.setText("Created By : "+getItem(position).nameReporter);
            ((VHItem) holder).txtTime.setText(DateUtils.getDate(getItem(position).timestamp));
            try {
                Glide.with(context).load(decodeFromFirebaseBase64(getItem(position).image)).asBitmap().
                        placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(((VHItem) holder).imgNews);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((VHItem) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favListActivity.setOnItemClick(position, ((VHItem) holder).imgNews);
                }
            });
        } else if (holder instanceof VHHeader) {
            ((VHHeader) holder).txtTitle.setText("Events & News");
            if(mNewsList.size() == 0) {
                ((VHHeader) holder).mProgress.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((VHHeader) holder).mProgress.setVisibility(View.GONE);
                    }
                }, 15000);
            }else{
                ((VHHeader) holder).mProgress.setVisibility(View.GONE);
            }
        }
    }

    public static  byte[]  decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        //return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedByteArray;
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

    private NewsPostModel getItem(int position) {
        return mNewsList.get(position - 1);
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

    class VHHeader extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtNoData;
        ProgressBar mProgress;
        public VHHeader(View itemView) {
            super(itemView);
            imgCover = (ImageView) itemView.findViewById(R.id.imgCover);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtNoData = (TextView) itemView.findViewById(R.id.txtNodata);
            mProgress = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }


}