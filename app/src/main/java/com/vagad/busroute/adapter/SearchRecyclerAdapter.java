package com.vagad.busroute.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.vagad.R;
import com.vagad.busroute.SearchCityActivity;
import com.vagad.model.BusListModel;
import com.vagad.utils.fonts.CustomFontTextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by ubuntu on 19/4/16.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BusListModel> mListBus;
    private Context mContext;
    private SearchCityActivity searchCityActivity;
    private static final String TAG = "SearchRecyclerAdapter";
    private String searchString = "";

    public void setSearchList(List<BusListModel> mListCity) {
        this.mListBus.clear();
        this.mListBus.addAll(mListCity);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CustomFontTextView mTxtSearch;

        public ViewHolder(View rowView) {
            super(rowView);
            mTxtSearch = (CustomFontTextView) rowView.findViewById(R.id.txt_search);
        }

        public void bindView(int pos, BusListModel busListModel) {
            mTxtSearch.setText(busListModel.NAME_OF_ROUTE);
        }
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public SearchRecyclerAdapter(Context context, List<BusListModel> mListBus, SearchCityActivity searchCityActivity) {
        this.mListBus = mListBus;
        mContext = context;
        this.searchCityActivity = searchCityActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.bindView(position, mListBus.get(position));

        vh.mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCityActivity.onClickCity(mListBus.get(position).NAME_OF_ROUTE);
            }
        });

        /**
         * This is for color change search text
         */
        String name = mListBus.get(position).NAME_OF_ROUTE.toLowerCase(Locale.getDefault());
        if (name.contains(searchString)) {
            int startPos = name.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(vh.mTxtSearch.getText());
            spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(vh.mTxtSearch.getContext(), R.color.colorAccent)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            vh.mTxtSearch.setText(spanText, TextView.BufferType.SPANNABLE);
        }
        /**
         * End
         */
    }

    @Override
    public int getItemCount() {
        return mListBus.size();
    }

}
