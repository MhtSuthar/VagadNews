package com.vagad.busroute.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;


import com.vagad.R;
import com.vagad.busroute.fragment.BusRouteSearchFragment;
import com.vagad.model.BusListModel;
import com.vagad.utils.fonts.CustomFontTextView;

import java.util.List;
import java.util.Random;


/**
 * Created by ubuntu on 19/4/16.
 */
public class BusRouteRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private View header;

    private List<BusListModel> mListBus;
    private Context mContext;
    private BusRouteSearchFragment busRouteSearchFragment;
    private static final String TAG = "BusRouteRecyclerAdapter";
    private int lastPosition = -1;
    private String searchString = "";

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CustomFontTextView mBusName;
        public CustomFontTextView mTxtArrival;
        public CustomFontTextView mTxtArrivalTime;
        public CustomFontTextView mTxtDeparture;
        public CustomFontTextView mTxtDepartureTime;
        public CustomFontTextView mTxtDistance;
        public CustomFontTextView mTxtVia;
        public CustomFontTextView mTxtArrivalA;
        public CustomFontTextView mTxtDepartureA;
        public LinearLayout mLinShare;

        public ViewHolder(View rowView) {
            super(rowView);
            mBusName = (CustomFontTextView) rowView.findViewById(R.id.txt_bus_name);
            mTxtArrival = (CustomFontTextView) rowView.findViewById(R.id.txt_arrival);
            mTxtArrivalTime = (CustomFontTextView) rowView.findViewById(R.id.txt_arrival_time);
            mTxtDeparture = (CustomFontTextView) rowView.findViewById(R.id.txt_departure);
            mTxtDepartureTime = (CustomFontTextView) rowView.findViewById(R.id.txt_departure_time);
            mTxtDistance = (CustomFontTextView) rowView.findViewById(R.id.txt_distance);
            mTxtVia = (CustomFontTextView) rowView.findViewById(R.id.txt_via);
            mTxtArrivalA = (CustomFontTextView) rowView.findViewById(R.id.txt_arrival_time_a);
            mTxtDepartureA = (CustomFontTextView) rowView.findViewById(R.id.txt_departure_time_a);
            mLinShare = (LinearLayout) rowView.findViewById(R.id.lin_share);
        }

        public void bindView(int pos, BusListModel busListModel) {
            mBusName.setText(busListModel.NAME_OF_ROUTE);
            mTxtDeparture.setText(busListModel.NAME_OF_ROUTE.contains("-") ? busListModel.NAME_OF_ROUTE.split("-")[0] : busListModel.NAME_OF_ROUTE);
            mTxtArrival.setText(busListModel.NAME_OF_ROUTE.contains("-") ? busListModel.NAME_OF_ROUTE.split("-")[1] : busListModel.NAME_OF_ROUTE);
            mTxtArrivalTime.setText(busListModel.ARR_TIME.split(" ")[0]);
            mTxtDepartureTime.setText(busListModel.DEP_TIME.split(" ")[0]);
            mTxtArrivalA.setText(busListModel.ARR_TIME.split(" ")[1]);
            mTxtDepartureA.setText(busListModel.DEP_TIME.split(" ")[1]);
            mTxtDistance.setText(busListModel.ROUTE_KMS+" KM");
            mTxtVia.setText(busListModel.VIA);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(View rowView) {
            super(rowView);

        }
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setSearchList(List<BusListModel> mListCity) {
        this.mListBus.clear();
        this.mListBus.addAll(mListCity);
        notifyDataSetChanged();
    }

    public BusRouteRecyclerAdapter(View header, Context context, List<BusListModel> mListBus, BusRouteSearchFragment busRouteSearchFragment) {
        if (header == null) {
            throw new IllegalArgumentException("header may not be null");
        }
        this.header = header;
        this.mListBus = mListBus;
        mContext = context;
        this.busRouteSearchFragment = busRouteSearchFragment;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new ViewHolderHeader(header);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bus_route, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }

        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            vh.bindView(position, mListBus.get(position-1));

            vh.mLinShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareRoute(mListBus.get(position-1));
                }
            });

            /**
             * This is for color change search text
             */
            /*String name = mListBus.get(position).NAME_OF_ROUTE.contains("-") ? mListBus.get(position).NAME_OF_ROUTE.split("-")[1].toLowerCase(Locale.getDefault()) : mListBus.get(position).NAME_OF_ROUTE.toLowerCase(Locale.getDefault());
            Log.e(TAG, "onBindViewHolder: "+name+" contains  "+searchString);
            if (name.contains(searchString)) {
                int startPos = name.indexOf(searchString);
                int endPos = startPos + searchString.length();

                Spannable spanText = Spannable.Factory.getInstance().newSpannable(vh.mTxtArrival.getText());
                spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(vh.mTxtArrival.getContext(), R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                vh.mTxtArrival.setText(spanText, TextView.BufferType.SPANNABLE);
            }

            String nameDeparture = mListBus.get(position).NAME_OF_ROUTE.contains("-") ? mListBus.get(position).NAME_OF_ROUTE.split("-")[0].toLowerCase(Locale.getDefault()) : mListBus.get(position).NAME_OF_ROUTE.toLowerCase(Locale.getDefault());
            if (nameDeparture.contains(searchString)) {
                int startPos = nameDeparture.indexOf(searchString);
                int endPos = startPos + searchString.length();

                Spannable spanText = Spannable.Factory.getInstance().newSpannable(vh.mTxtDeparture.getText());
                spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(vh.mTxtDeparture.getContext(), R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                vh.mTxtDeparture.setText(spanText, TextView.BufferType.SPANNABLE);
            }*/
            /**
             * End
             */
        }



        //setAnimation(holder.itemView, position);
    }

    private void shareRoute(BusListModel busListModel) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check out Bus detail : "+busListModel.NAME_OF_ROUTE+"\n Timing :"+busListModel.DEP_TIME+" to "+busListModel.ARR_TIME+" " +
                            "\nVia : "+busListModel.VIA+", Dist. : "+busListModel.ROUTE_KMS+" KM\nThanks for using Vagad News App & Download from : https://play.google.com/store/apps/details?id=com.vagad");
            sendIntent.setType("text/plain");
            mContext.startActivity(sendIntent);
    }

    @Override
    public int getItemCount() {
        return mListBus.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

}
