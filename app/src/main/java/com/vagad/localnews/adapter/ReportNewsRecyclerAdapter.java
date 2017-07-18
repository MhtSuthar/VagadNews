package com.vagad.localnews.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vagad.R;
import com.vagad.localnews.fragment.ReporterNewsListFragment;
import com.vagad.model.NewsPostModel;
import com.vagad.utils.AppUtils;
import com.vagad.utils.DateUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ReportNewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<NewsPostModel> mNewsList;
    private Context context;
    private ReporterNewsListFragment reporterNewsListActivity;

    public ReportNewsRecyclerAdapter(List<NewsPostModel> mNewsList, Context context, ReporterNewsListFragment favListActivity) {
        this.mNewsList = mNewsList;
        this.context = context;
        this.reporterNewsListActivity = favListActivity;
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
            if(AppUtils.getUniqueId(context).equals(getItem(position).uniqueId))
                ((VHItem) holder).img_more.setVisibility(View.VISIBLE);
            else
                ((VHItem) holder).img_more.setVisibility(View.GONE);
            ((VHItem) holder).img_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, ((VHItem) holder).img_more);
                    popup.inflate(R.menu.menu_news_item);
                    setForceShowIcon(popup);
                    //MenuPopupHelper menuHelper = new MenuPopupHelper( ((VHItem) holder).img_more.getContext(), (MenuBuilder) popup.getMenu(), ((VHItem) holder).img_more);
                    //menuHelper.setForceShowIcon(true);
                    //menuHelper.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    reporterNewsListActivity.onEditReport(getItem(position));
                                    break;
                                case R.id.menu_delete:
                                    reporterNewsListActivity.onDeleteReport(getItem(position), position);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
            try {
                Glide.with(context).load(decodeFromFirebaseBase64(getItem(position).image)).asBitmap().
                        placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(((VHItem) holder).imgNews);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((VHItem) holder).mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reporterNewsListActivity.setOnItemClick(position, ((VHItem) holder).imgNews);
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

    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static  byte[]  decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        //return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        return decodedByteArray;
    }

    @Override
    public int getItemCount() {
        return mNewsList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
       /* if (isPositionHeader(position))
            return TYPE_HEADER;*/

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private NewsPostModel getItem(int position) {
        return mNewsList.get(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        public ImageView imgNews, img_more;
        public TextView txtTitle, txtTime, txtDescription;
        public CardView mCardView;
        public VHItem(View itemView) {
            super(itemView);
            imgNews = (ImageView) itemView.findViewById(R.id.imgNews);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            img_more = (ImageView) itemView.findViewById(R.id.img_more);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
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