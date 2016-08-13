package com.example.amankumar.lucidme.UI.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatMessageModel;
import com.example.amankumar.lucidme.Model.ChatModel;
import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectFriendActivity extends AppCompatActivity {
    SharedPreferences sp;
    String currentUser, listId, selectedUser, chatId, message;
    Toolbar toolbar;
    ListView selectList;
    FirebaseListAdapter<ChatModel> listAdapter;
    FirebaseDatabase ref;
    DatabaseReference chatRef, messageRef, dreamRef;
    //sender
    DatabaseReference senderSharedRef, senderChatRef;
    //receiver
    DatabaseReference receiverSharedRef, receiverChatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        toolbar = (Toolbar) findViewById(R.id.SF_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Friend");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        listAdapter = new FirebaseListAdapter<ChatModel>(this, ChatModel.class, R.layout.choose_friend_list_view, chatRef) {
            @Override
            protected void populateView(View v, ChatModel model, int position) {
                String userName = model.getUserName();
                TextView userNameText = (TextView) v.findViewById(R.id.SF_userName_LV);
                TextView userEmailText = (TextView) v.findViewById(R.id.SF_userEmail_LV);
                userNameText.setText(userName);
                String selectedEmail = listAdapter.getRef(position).getKey();
                selectedEmail=Utils.decodeEmail(selectedEmail);
                userEmailText.setText(selectedEmail);
            }
        };
        selectList.setAdapter(listAdapter);
        selectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = listAdapter.getRef(position).getKey();
                //sender
                senderChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS).child(selectedUser);
                senderSharedRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_SHARED_DREAM).child(selectedUser);
                //receiver
                receiverChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(selectedUser).child(Constants.LOCATION_CHATS).child(currentUser);
                receiverSharedRef = ref.getReference().child(Constants.LOCATION_USERS).child(selectedUser).child(Constants.LOCATION_SHARED_DREAM).child(currentUser);
                dreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        senderSharedRef.child(listId).setValue(dataSnapshot.getValue());
                        receiverSharedRef.child(listId).setValue(dataSnapshot.getValue());
                        Dream model = dataSnapshot.getValue(Dream.class);
                        String title = model.getTitleDream();
                        int comp = Utils.compareEmail(currentUser, selectedUser);
                        if (comp <= 0) {
                            chatId = currentUser + Constants.CONSTANT_AND + selectedUser;
                        } else {
                            chatId = selectedUser + Constants.CONSTANT_AND + currentUser;
                        }
                        messageRef = ref.getReference().child(Constants.LOCATION_USER_CHATS).child(chatId);
                        ChatMessageModel messageModel = new ChatMessageModel(title, listId, currentUser);
                        messageRef.push().setValue(messageModel);

                        receiverChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                                String receiverUserName = chatModel1.getUserName();
                                String receiverMessage = receiverUserName + ": " + message;
                                ChatModel receiverModel = new ChatModel(receiverUserName, receiverMessage);
                                receiverChatRef.setValue(receiverModel);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        senderChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                                String senderUserName = chatModel.getUserName();
                                String senderMessage = "You: " + message;
                                ChatModel senderModel = new ChatModel(senderUserName, senderMessage);
                                senderChatRef.setValue(senderModel);
                                Intent intent = new Intent(SelectFriendActivity.this, ChatDetailActivity.class);
                                intent.putExtra("listId", selectedUser);
                                intent.putExtra("name", senderUserName);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void init() {
        listId = getIntent().getStringExtra("listId");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        message = "Read this dream of mine";
        chatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
        dreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS).child(listId);
        selectList = (ListView) findViewById(R.id.SF_list_view);
    }
}
