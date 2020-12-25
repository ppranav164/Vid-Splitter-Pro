package com.card.splitter_pro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class Notifications {


    public static void showNotification(Context context,String title,String message,int smallIcon)
    {
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("alert",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        notificationBuilder.setLights(Color.RED, 3000, 3000);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setSmallIcon(smallIcon);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(sound);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notificationBuilder.build());
    }


}
