package com.vagad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.vagad.utils.AlarmUtils.setAlarmForDeleteNews;
import static com.vagad.utils.AlarmUtils.setAlarm;

/**
 * Created by ubuntu on 6/7/16.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
        setAlarmForDeleteNews(context);
    }
}
