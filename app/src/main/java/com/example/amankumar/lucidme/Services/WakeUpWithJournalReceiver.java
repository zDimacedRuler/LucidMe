package com.example.amankumar.lucidme.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.DreamActivity;

/**
 * Created by AmanKumar on 5/16/2016.
 */
public class WakeUpWithJournalReceiver extends BroadcastReceiver {
    NotificationCompat.Builder notification;
    public static final int UNIQUE_ID = 1234;

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true);
        notification.setTicker("Tap to enter you dream");
        notification.setWhen(System.currentTimeMillis());
        notification.setSmallIcon(R.drawable.ic_lucidme_notif);
        notification.setSound(sound);
        notification.setContentTitle("Its time to record your dream");
        notification.setContentText("Tap to enter your dream");
        Intent myIntent = new Intent(context, DreamActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(UNIQUE_ID, notification.build());
        Toast.makeText(context, "Someone called me", Toast.LENGTH_SHORT).show();
    }
}
