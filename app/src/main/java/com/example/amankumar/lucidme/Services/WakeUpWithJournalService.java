package com.example.amankumar.lucidme.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.DreamActivity;

public class WakeUpWithJournalService extends IntentService {
    NotificationCompat.Builder notification;
    public static final int UNIQUE_ID=1234;

    public WakeUpWithJournalService() {
        super("WakeUpWithJournalService");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification=new NotificationCompat.Builder(this.getApplicationContext());
        notification.setAutoCancel(true);
        notification.setTicker("Tap to enter you dream");
        notification.setSmallIcon(R.mipmap.ic_lucidme);
        notification.setWhen(System.currentTimeMillis());
        notification.setSound(sound);
        notification.setContentTitle("Its time to record your dream");
        notification.setContentText("Tap to enter your dream");
        Intent myIntent=new Intent(this.getApplicationContext(), DreamActivity.class);
        myIntent.putExtra("caller","Home");
        PendingIntent pendingIntent=PendingIntent.getActivity(this.getApplicationContext(),0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(UNIQUE_ID,notification.build());
    }
}
