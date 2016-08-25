package com.example.florian.bluetoothvolumeadapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Florian on 25/08/2016.
 */
public class EarphoneModeChooserNotificationManager {
    static void createNotification(Context context) {
        //notification intent
        Intent notificationIntent = new Intent(context, EarphoneModeChooserActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        //notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getResources().getString(R.string.earphone_notification_title));
        notificationBuilder.setContentText(context.getResources().getString(R.string.earphone_notification_subtitle));
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notification);
    }

    static void deleteNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        try {
            mNotificationManager.cancel(1);
        }
        catch (Exception e) {}
    }
}
