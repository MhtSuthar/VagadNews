package com.vagad.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vagad.receiver.AlarmReceiver;
import com.vagad.receiver.AlarmReceiverDeleteNews;
import com.vagad.storage.SharedPreferenceUtil;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Admin on 19-Feb-17.
 */

public class AlarmUtils {

    private static final String TAG = "AlarmUtils";
    public static final int ALARM_ID_FOR_DELETE_NEWS = 101;
    public static final int ALARM_ID_FOR_NEWS = 102;

    public static void setAlarm(Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        //calendar.add(Calendar.DAY_OF_MONTH, 1);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID_FOR_NEWS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);

        SharedPreferenceUtil.putValue(Constants.KEY_IS_ALARM_SETUP, true);
        SharedPreferenceUtil.save();
        Log.e(TAG, "setAlarm: "+calendar.getTime());
    }

    public static void cancelAlarm(Context context, int alarmId){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static void setAfterFiveDaysAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 5);
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiverDeleteNews.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID_FOR_DELETE_NEWS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), alarmIntent);
        Log.e(TAG, "setDeleteAlarm: "+calendar.getTime());
    }
}
