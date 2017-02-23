package com.vagad.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.vagad.R;
import com.vagad.model.RSSItem;
import com.vagad.storage.RSSDatabaseHandler;

import java.util.List;

public class WidgetListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;
    private RSSDatabaseHandler rssDatabaseHandler;
    private List<RSSItem> mListNews;
    private static final String TAG = "WidgetListViewsFactory";
    private Typeface typeface;

    public WidgetListViewsFactory(Context ctxt, Intent intent) {
        this.context = ctxt;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        rssDatabaseHandler = new RSSDatabaseHandler(context);
        mListNews = rssDatabaseHandler.getLatestNews();
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/DroidHindi.ttf");
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return (mListNews.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.widget_list_item);

        row.setTextViewText(android.R.id.text1, mListNews.get(position).getTitle());
        //row.setImageViewBitmap(android.R.id.text1, createTextBitmap(mListNews.get(position).getTitle()));
        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(WidgetProvider.EXTRA_WORD, "" + mListNews.get(position).getId());
        extras.putString(WidgetProvider.EXTRA_WIDGET_ID, "" + appWidgetId);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);

        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }

    public Bitmap createTextBitmap(final String text) {
        Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(text, 80, 60, paint);
        return myBitmap;
    }
}