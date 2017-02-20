package com.vagad.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vagad.receiver.AlarmReceiver;
import com.vagad.storage.SharedPreferenceUtil;

import java.util.Calendar;

/**
 * Created by Admin on 19-Feb-17.
 */

public class AlarmUtils {

    private static final String TAG = "AlarmUtils";

    public static void setAlarm(Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        //calendar.add(Calendar.DAY_OF_MONTH, 1);
        Log.e(TAG, "setAlarm: "+calendar.getTime());
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);

        SharedPreferenceUtil.putValue(Constants.KEY_IS_ALARM_SETUP, true);
        SharedPreferenceUtil.save();
    }

    public static void setAfterTwoHourAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);

    }
}
