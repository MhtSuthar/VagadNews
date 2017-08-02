package com.vagad.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.vagad.R;
import com.vagad.dashboard.NewsListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by mht on 28/9/16.
 */

public class NotificationUtils {

    public NotificationUtils() {
    }

    public synchronized void generateNotification(Context context, String title) {
        String summaryText = title;

        Intent resultIntent = new Intent(context, NewsListActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(summaryText);
        bigText.setBigContentTitle(context.getString(R.string.app_name));
        //bigText.setSummaryText("");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        notification = new NotificationCompat.Builder(context)
                .setCategory(Notification.CATEGORY_PROMO)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(summaryText)
                .setSmallIcon(getNotificationIcon())
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(icon1)
                .setAutoCancel(true)
                .setStyle(bigText)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND).build();
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    public synchronized void generateNotification(Context context, String title, String message, boolean isFromMobile) {
        String summaryText = message;

        Intent resultIntent = new Intent(context, NewsListActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra(Constants.EXTRA_FROM_LOCALE_NEWS, isFromMobile);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(summaryText);
        bigText.setBigContentTitle(context.getString(R.string.app_name));
        //bigText.setSummaryText("");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        notification = new NotificationCompat.Builder(context)
                .setCategory(Notification.CATEGORY_PROMO)
                .setContentTitle(title)
                .setContentText(summaryText)
                .setSmallIcon(getNotificationIcon())
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(icon1)
                .setAutoCancel(true)
                .setStyle(bigText)
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND).build();
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    public synchronized void showBigNotification(String imagePath, String message, Context context) {
        new GetImageAsynk(imagePath, message, context).execute();
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100);
    }

    public int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_white_notification : R.mipmap.ic_launcher;
    }

    class GetImageAsynk extends AsyncTask<Void, Void, Bitmap>{

        private String imagePath, message;
        private Context context;

        public GetImageAsynk(String imagePath, String message, Context context) {
            this.imagePath = imagePath;
            this.message = message;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return getBitmapFromURL(imagePath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
                Intent resultIntent = new Intent(context, NewsListActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0, resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(context.getString(R.string.app_name));
                bigPictureStyle.setSummaryText(message);
                bigPictureStyle.bigPicture(bitmap);
                Notification notification;
                notification = new NotificationCompat.Builder(context).setTicker(context.getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentIntent(resultPendingIntent)
                        .setStyle(bigPictureStyle)
                        .setSmallIcon(getNotificationIcon())
                        .setSound(defaultSoundUri)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setContentText(message)
                        .build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }else{
                generateNotification(context, message);
            }
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
