package com.example.amankumar.lucidme.UI.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
                TextView leftTime= (TextView) v.findViewById(R.id.leftmessageTimeText);
                TextView rightTime= (TextView) v.findViewById(R.id.rightMessageTimeText);
                LinearLayout leftMessageLinear= (LinearLayout) v.findViewById(R.id.leftMessageLinear);
                LinearLayout rightMessageLinear= (LinearLayout) v.findViewById(R.id.rightMessageLinear);
                HashMap<String, Object> map = model.getMessageTimestamp();
                Long milli = (Long) map.get(Constants.CONSTANT_TIMESTAMP);
                String time;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milli);
                int hour = calendar.get(Calendar.HOUR);
                if(hour==0)
                    hour=12;
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
                    String text=model.getMessage();
                    if(text.length()<=20) {
                        rightMessageLinear.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    else{
                        rightMessageLinear.setOrientation(LinearLayout.VERTICAL);
                    }
                    rightMessageText.setText(text);
                    rightTime.setText(time);
                    leftMessageLinear.setVisibility(View.GONE);
                } else {
                    leftMessageLinear.setVisibility(View.VISIBLE);
                    String text=model.getMessage();
                    if(text.length()<=20) {
                        leftMessageLinear.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    else{
                        leftMessageLinear.setOrientation(LinearLayout.VERTICAL);
                    }
                    leftMessageText.setText(text);
                    leftTime.setText(time);
                    rightMessageLinear.setVisibility(View.GONE);
                }
            }
        };
        chatListView.setAdapter(listAdapter);
        chatListView.setDivider(null);
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
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

    }

    public void sendMessageHandler(View view) {
        message = messageEdit.getText().toString();
        if (message.equals(""))
            return;
        messageRef = ref.getReference().child(Constants.LOCATION_USER_CHATS).child(chatId);
        final ChatMessageModel messageModel = new ChatMessageModel(currentUser, message);
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
                String receiverUserName = chatModel1.getUserName();
                Log.e("CHat Detail", receiverUserName);
                String receiverMessage = receiverUserName + ": " + message;
                ChatModel receiverModel = new ChatModel(receiverUserName, receiverMessage);
                receiverChatRef.setValue(receiverModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
