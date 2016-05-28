package com.example.amankumar.lucidme.UI.Chat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;


public class ChatFragment extends Fragment {
    String currentUser;
    SharedPreferences sp;
    FirebaseDatabase ref;
    DatabaseReference userRef,chatRef;
    ListView chatList;
    CardView noChatCard;
    Query query;
    String listId;
    FirebaseListAdapter<ChatModel> listAdapter;
    public ChatFragment() {
        // Required empty public constructor
    }
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        sp= PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUser=sp.getString(Constants.CURRENT_USER,null);
        ref=FirebaseDatabase.getInstance();
        userRef=ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
        chatList= (ListView) view.findViewById(R.id.chatListView);
        noChatCard= (CardView) view.findViewById(R.id.CF_no_chat_card_view);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Constants.LOCATION_CHATS).exists()){
                    noChatCard.setVisibility(View.GONE);
                    chatRef=ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
                    listAdapter=new FirebaseListAdapter<ChatModel>(getActivity(),ChatModel.class,R.layout.chat_list_view,chatRef) {
                        @Override
                        protected void populateView(View v, ChatModel model, int position) {
                            TextView nameText= (TextView) v.findViewById(R.id.chat_list_userName);
                            TextView timeText= (TextView) v.findViewById(R.id.chat_list_time);
                            TextView messageText= (TextView) v.findViewById(R.id.chat_list_message);
                            nameText.setText(model.getUserName());
                            String time;
                            messageText.setText(model.getLastMessage());
                            HashMap<String,Object> obj=model.getLastUpdatedTimeStamp();
                            long milli= (long) obj.get(Constants.CONSTANT_TIMESTAMP);
                            Calendar calendar=Calendar.getInstance();
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
                            timeText.setText(time);
                        }
                    };
                    chatList.setAdapter(listAdapter);
                    chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            listId = listAdapter.getRef(position).getKey();
                            ChatModel chatModel = listAdapter.getItem(position);
                            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                            intent.putExtra("listId", listId);
                            intent.putExtra("name", chatModel.getUserName());
                            startActivity(intent);
                        }
                    });
                }else{
                    noChatCard.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
