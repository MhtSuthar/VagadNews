package com.vagad.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.vagad.R;
import com.vagad.dashboard.HomeActivity;

public class WidgetProvider extends AppWidgetProvider {

  public static String EXTRA_WORD =
    "com.commonsware.android.appwidget.lorem.WORD";
  public static String EXTRA_WIDGET_ID =
          "com.commonsware.android.appwidget.lorem";

  @Override
  public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                       int[] appWidgetIds) {
    Log.e("onUpdate", "onUpdate: "+appWidgetIds);
    for (int i=0; i<appWidgetIds.length; i++) {
      Intent svcIntent=new Intent(ctxt, WidgetService.class);
      
      svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
      
      RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
                                          R.layout.widget_list_view);

      Log.e("appWidgetIds[i]", "onUpdate: "+appWidgetIds[i]);
      
      widget.setRemoteAdapter(R.id.words,
                              svcIntent);

      Intent clickIntent=new Intent(ctxt, HomeActivity.class);
      PendingIntent clickPI= PendingIntent
                              .getActivity(ctxt, 0,
                                            clickIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
      
      widget.setPendingIntentTemplate(R.id.words, clickPI);

      appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
    }
    
    super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
  }
}