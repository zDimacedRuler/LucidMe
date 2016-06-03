package com.example.amankumar.lucidme.UI.Chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatMessageModel;
import com.example.amankumar.lucidme.Model.ChatModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindChatActivity extends AppCompatActivity {
    public static final String RECIPIENT_NAME = "recipient_name";
    SharedPreferences sp;
    String currentUser;
    FirebaseDatabase ref;
    DatabaseReference userRef, senderChatRef, recipientChatRef, userChatRef, nameRef;
    LinearLayout messageLinear, resultLinear;
    EditText findUserEdit, messageEdit;
    ImageView findUserButton, userProfileImage;
    TextView resultText;
    Button sendMessageButton;
    String findUserMail;
    String encodedFindUserMail;
    Toolbar toolbar;
    String message, senderMessage, recipientMessage, chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_chat);
        toolbar = (Toolbar) findViewById(R.id.FC_appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, null);
        init();
        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                findFriend();
            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        messageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN)){
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    findFriend();
                }
                return true;
            }
        });
    }

    private void sendMessage() {
        message = messageEdit.getText().toString();
        if (message.equals(""))
            return;
        nameRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String senderName = dataSnapshot.child(Constants.LOCATION_USERNAME).getValue().toString();
                senderChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
                String recipientName = sp.getString(RECIPIENT_NAME, "");
                recipientMessage = "You: " + message;
                ChatModel senderChatModel = new ChatModel(recipientName, recipientMessage);
                senderChatRef.child(encodedFindUserMail).setValue(senderChatModel);
                recipientChatRef = ref.getReference().child(Constants.LOCATION_USERS).child(encodedFindUserMail).child(Constants.LOCATION_CHATS);
                senderMessage = senderName + ": " + message;
                ChatModel reciverChatModel = new ChatModel(senderName, senderMessage);
                recipientChatRef.child(currentUser).setValue(reciverChatModel);
                String author = currentUser;
                userChatRef = ref.getReference().child(Constants.LOCATION_USER_CHATS);
                int comp = Utils.compareEmail(currentUser, encodedFindUserMail);
                if (comp <= 0) {
                    chatId = currentUser + Constants.CONSTANT_AND + encodedFindUserMail;
                } else {
                    chatId = encodedFindUserMail + Constants.CONSTANT_AND + currentUser;
                }
                ChatMessageModel messageModel = new ChatMessageModel(author, message);
                DatabaseReference listRef = userChatRef.child(chatId).push();
                listRef.setValue(messageModel);
                Intent intent=new Intent(FindChatActivity.this,ChatDetailActivity.class);
                intent.putExtra("listId",encodedFindUserMail);
                intent.putExtra("name", recipientName);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString(RECIPIENT_NAME, null).apply();
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void findFriend() {
        findUserMail = findUserEdit.getText().toString();
        if (findUserMail.equals(""))
            return;
        encodedFindUserMail = Utils.encodeEmail(findUserMail);
        if (encodedFindUserMail.equals(currentUser)){
            resultLinear.setVisibility(View.VISIBLE);
            userProfileImage.setVisibility(View.GONE);
            resultText.setGravity(Gravity.CENTER);
            resultText.setText("Cannot make yourself a friend!");
            messageLinear.setVisibility(View.GONE);
            return;
        }
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(encodedFindUserMail).exists()) {
                    resultLinear.setVisibility(View.VISIBLE);
                    userProfileImage.setVisibility(View.VISIBLE);
                    String name = (String) dataSnapshot.child(encodedFindUserMail).getValue();
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(RECIPIENT_NAME, name).apply();
                    messageLinear.setVisibility(View.VISIBLE);
                    resultText.setGravity(Gravity.CENTER_VERTICAL);
                    resultText.setText(name);
                } else {
                    resultLinear.setVisibility(View.VISIBLE);
                    userProfileImage.setVisibility(View.GONE);
                    resultText.setGravity(Gravity.CENTER);
                    resultText.setText("User not found");
                    messageLinear.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        ref = FirebaseDatabase.getInstance();
        userRef = ref.getReference().child(Constants.LOCATION_USER_DETAILS);
        messageLinear = (LinearLayout) findViewById(R.id.FC_message_linear);
        resultLinear = (LinearLayout) findViewById(R.id.FC_result_linear);
        findUserEdit = (EditText) findViewById(R.id.FC_find_user_edit);
        messageEdit = (EditText) findViewById(R.id.FC_message_edit);
        findUserButton = (ImageView) findViewById(R.id.FC_find_user_button);
        resultText = (TextView) findViewById(R.id.FC_result_text);
        userProfileImage = (ImageView) findViewById(R.id.FC_user_profile);
        sendMessageButton = (Button) findViewById(R.id.FC_message_send_button);
    }
}
