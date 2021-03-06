package com.vagad.utils;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 18-Feb-17.
 */

public class DateUtils {

    private static final String TAG = "DateUtils";

    public static String convertData(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(date));
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String convertTimestamp(String pubdate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(pubdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ""+date.getTime();
    }

    public static String convertTimestampRajasthan(String pubdate) {
        Date date = null;
        try {
            //Tuesday, August 15, 2017 09:15 AM
            date = new SimpleDateFormat("EEEE, MMMM dd, yyyy KK:mm a").parse(pubdate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return ""+date.getTime();
    }

    public static String getTimestamp() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static String getDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        Date df = new java.util.Date(time);
        String date = simpleDateFormat.format(df).toString();
        return date;
    }
}
