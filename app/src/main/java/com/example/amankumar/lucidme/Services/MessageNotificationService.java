package com.example.amankumar.lucidme.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.amankumar.lucidme.Model.ChatMessageModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.Chat.ChatDetailActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MessageNotificationService extends Service {
    private Handler mHandler;
    private Runnable mRunnable;
    SharedPreferences sp;
    FirebaseDatabase ref;
    DatabaseReference chatRef;
    String currentUser, chatId;
    NotificationCompat.Builder notification;
    Boolean foreground;
    Boolean newItems;
    String foregroundUser;

    public MessageNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                doTask();
            }

        };
        mHandler.post(mRunnable);
        return START_STICKY;
    }

    private void doTask() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        chatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatKey = snapshot.getKey();
                    final String userName = (String) snapshot.child("userName").getValue();
                    int comp = Utils.compareEmail(currentUser, chatKey);
                    if (comp <= 0) {
                        chatId = currentUser + Constants.CONSTANT_AND + chatKey;
                    } else {
                        chatId = chatKey + Constants.CONSTANT_AND + currentUser;
                    }
                    DatabaseReference chat = ref.getReference().child(Constants.LOCATION_USER_CHATS).child(chatId);
                    Query query = chat.limitToLast(1);
                    newItems = false;
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (!newItems) return;
                            ChatMessageModel model = dataSnapshot.getValue(ChatMessageModel.class);
                            String message = model.getMessage();
                            String author = model.getAuthor();
                            foreground = sp.getBoolean(Constants.FOREGROUND, false);
                            foregroundUser=sp.getString(Constants.FOREGROUND_USER,"");
                            if (!author.equals(currentUser) && !author.equals(foregroundUser)) {
                                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                notification = new NotificationCompat.Builder(MessageNotificationService.this);
                                notification.setAutoCancel(true);
                                notification.setSmallIcon(R.drawable.ic_lucidme_notif);
                                notification.setWhen(System.currentTimeMillis());
                                notification.setSound(sound);
                                notification.setContentTitle(userName);
                                notification.setContentText(message);
                                Intent myIntent = new Intent(MessageNotificationService.this, ChatDetailActivity.class);
                                myIntent.putExtra("listId", author);
                                myIntent.putExtra("name", userName);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MessageNotificationService.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notification.setContentIntent(pendingIntent);
                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                manager.notify(author.hashCode(), notification.build());
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newItems = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }
}
