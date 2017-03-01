package com.vagad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vagad.storage.RSSDatabaseHandler;

/**
 * Created by Admin on 23-Feb-17.
 */

public class AlarmReceiverDeleteNews extends BroadcastReceiver {

    private RSSDatabaseHandler rssDatabaseHandler;
    private static final String TAG = "AlarmReceiverDeleteNews";

    @Override
    public void onReceive(Context context, Intent intent) {
        rssDatabaseHandler = new RSSDatabaseHandler(context);
        rssDatabaseHandler.deleteFeedWithList();
    }
}
