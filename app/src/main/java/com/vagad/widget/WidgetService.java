package com.vagad.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {

	private static final String TAG = "WidgetService";

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return(new WidgetListViewsFactory(this.getApplicationContext(),
				intent));
	}
}