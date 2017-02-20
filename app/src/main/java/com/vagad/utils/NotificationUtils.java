package com.vagad.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.vagad.R;
import com.vagad.dashboard.HomeActivity;


/**
 * Created by mht on 28/9/16.
 */

public class NotificationUtils {

    public NotificationUtils() {
    }

    public void generateNotification(Context context, String title) {
        String summaryText = title;

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_back);
        Bitmap bitmap_image = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash_bg);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
        s.setSummaryText(summaryText);
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
                        .setStyle(s)
                        .setContentIntent(resultPendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND).build();
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }


    public void updateNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100);
    }

    public int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_back : R.drawable.ic_back;
    }
}
