package com.example.amankumar.lucidme.UI.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatMessageModel;
import com.example.amankumar.lucidme.Model.ChatModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class ChatDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    String listId, userName;
    String currentUser, chatId, message, senderUserName;
    FirebaseDatabase ref;
    DatabaseReference senderChatRef, receiverChatRef, userChatRef, messageRef;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    ListView chatListView;
    FirebaseListAdapter<ChatMessageModel> listAdapter;
    EditText messageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        toolbar = (Toolbar) findViewById(R.id.Cd_appBar);
        setSupportActionBar(toolbar);
        listId = getIntent().getStringExtra("listId");
        userName = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(userName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        userChatRef = ref.getReference().child(Constants.LOCATION_USER_CHATS).child(chatId);
        listAdapter = new FirebaseListAdapter<ChatMessageModel>(this, ChatMessageModel.class, R.layout.chat_detail_list_view, userChatRef) {
            @Override
            protected void populateView(View v, ChatMessageModel model, int position) {
                TextView leftMessageText = (TextView) v.findViewById(R.id.leftmessageText_LV);
                TextView rightMessageText = (TextView) v.findViewById(R.id.rightmessageText_LV);
                TextView leftTime = (TextView) v.findViewById(R.id.leftmessageTimeText);
                TextView rightTime = (TextView) v.findViewById(R.id.rightMessageTimeText);
                LinearLayout leftMessageLinear = (LinearLayout) v.findViewById(R.id.leftMessageLinear);
                LinearLayout rightMessageLinear = (LinearLayout) v.findViewById(R.id.rightMessageLinear);
                Button leftButton = (Button) v.findViewById(R.id.leftMessageButton);
                Button rightButton = (Button) v.findViewById(R.id.rightMessageButton);
                HashMap<String, Object> map = model.getMessageTimestamp();
                Long milli = (Long) map.get(Constants.CONSTANT_TIMESTAMP);
                final String time, dreamId;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milli);
                int hour = calendar.get(Calendar.HOUR);
                if (hour == 0)
                    hour = 12;
                int min = calendar.get(Calendar.MINUTE);
                int amOrPm = calendar.get(Calendar.AM_PM);
                if (amOrPm == 0) {
                    time = hour + ":" + min + " AM";
                } else {
                    time = hour + ":" + min + " PM";
                }
                String author = model.getAuthor();
                if (author.equals(currentUser)) {
                    rightMessageLinear.setVisibility(View.VISIBLE);
                    String text = model.getMessage();
                    rightTime.setText(time);
                    leftMessageLinear.setVisibility(View.GONE);
                    if (model.getSharedDream().equals("false")) {
                        rightMessageText.setVisibility(View.VISIBLE);
                        if (text.length() <= 20) {
                            rightMessageLinear.setOrientation(LinearLayout.HORIZONTAL);
                        } else {
                            rightMessageLinear.setOrientation(LinearLayout.VERTICAL);
                        }
                        rightMessageText.setText(text);
                        rightButton.setVisibility(View.GONE);
                    } else {
                        rightMessageLinear.setOrientation(LinearLayout.VERTICAL);
                        rightButton.setVisibility(View.VISIBLE);
                        dreamId = model.getSharedDream();
                        rightButton.setText(text);
                        rightButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatDetailActivity.this, ReadSharedDream.class);
                                intent.putExtra("dreamId", dreamId);
                                intent.putExtra("sharedUser", listId);
                                intent.putExtra("userName", "You");
                                startActivity(intent);
                            }
                        });
                        rightMessageText.setVisibility(View.GONE);
                    }
                } else {
                    leftMessageLinear.setVisibility(View.VISIBLE);
                    rightMessageLinear.setVisibility(View.GONE);
                    String text = model.getMessage();
                    leftTime.setText(time);
                    if (model.getSharedDream().equals("false")) {
                        leftButton.setVisibility(View.GONE);
                        if (text.length() <= 20) {
                            leftMessageLinear.setOrientation(LinearLayout.HORIZONTAL);
                        } else {
                            leftMessageLinear.setOrientation(LinearLayout.VERTICAL);
                        }
                        leftMessageText.setVisibility(View.VISIBLE);
                        leftMessageText.setText(text);
                    } else {
                        leftMessageText.setVisibility(View.VISIBLE);
                        leftMessageLinear.setOrientation(LinearLayout.VERTICAL);
                        leftButton.setVisibility(View.VISIBLE);
                        dreamId = model.getSharedDream();
                        leftButton.setText(text);
                        leftButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatDetailActivity.this, ReadSharedDream.class);
                                intent.putExtra("dreamId", dreamId);
                                intent.putExtra("sharedUser", listId);
                                intent.putExtra("userName", userName);
                                startActivity(intent);
                            }
                        });
                        leftMessageText.setVisibility(View.GONE);
                    }
                }
            }
        };
        chatListView.setAdapter(listAdapter);
        chatListView.setDivider(null);
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spe=sp.edit();
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        chatListView = (ListView) findViewById(R.id.CD_list_view);
        messageEdit = (EditText) findViewById(R.id.CD_message_edit);
        senderChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS).child(listId);
        receiverChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(listId).child(Constants.LOCATION_CHATS).child(currentUser);
        int comp = Utils.compareEmail(currentUser, listId);
        if (comp <= 0) {
            chatId = currentUser + Constants.CONSTANT_AND + listId;
        } else {
            chatId = listId + Constants.CONSTANT_AND + currentUser;
        }
        if(!Utils.haveNetworkConnection(this)){
           Snackbar.make(toolbar,"No network connection",Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public void sendMessageHandler(View view) {
        message = messageEdit.getText().toString();
        if (message.equals(""))
            return;
        messageRef = ref.getReference().child(Constants.LOCATION_USER_CHATS).child(chatId);
        ChatMessageModel messageModel = new ChatMessageModel(currentUser, message);
        messageRef.push().setValue(messageModel);
        messageEdit.setText("");
        senderChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                senderUserName = chatModel.getUserName();
                String senderMessage = "You: " + message;
                ChatModel senderModel = new ChatModel(senderUserName, senderMessage);
                senderChatRef.setValue(senderModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        receiverChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                Log.d("Chat detail",dataSnapshot.toString());
                String receiverUserName = chatModel1.getUserName();
                String receiverMessage = receiverUserName + ": " + message;
                ChatModel receiverModel = new ChatModel(receiverUserName, receiverMessage);
                receiverChatRef.setValue(receiverModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        spe.putBoolean(Constants.FOREGROUND,true).apply();
        spe.putString(Constants.FOREGROUND_USER,listId).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        spe.putBoolean(Constants.FOREGROUND,false).apply();
        spe.putString(Constants.FOREGROUND_USER,"").apply();

    }
}
