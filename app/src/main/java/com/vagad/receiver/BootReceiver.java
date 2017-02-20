package com.vagad.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vagad.dashboard.HomeActivity;

import java.util.Calendar;

import static com.vagad.utils.AlarmUtils.setAlarm;

/**
 * Created by ubuntu on 6/7/16.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarm(context);
    }
}
