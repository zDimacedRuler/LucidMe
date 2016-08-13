package com.example.amankumar.lucidme.UI.Chat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;


public class ChatFragment extends Fragment {
    private final long ONE_MEGABYTE = 1024 * 1024;
    String currentUser;
    SharedPreferences sp;
    FirebaseDatabase ref;
    DatabaseReference userRef, chatRef;
    ListView chatList;
    CardView noChatCard;
    Query query;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, profileRef;
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
        userRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
        //download profile images every time apps loads--need to change this
        //downloadUserProfileImages();
        chatList = (ListView) view.findViewById(R.id.chatListView);
        noChatCard = (CardView) view.findViewById(R.id.CF_no_chat_card_view);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constants.LOCATION_CHATS).exists()) {
                    noChatCard.setVisibility(View.GONE);
                    chatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
                    query=chatRef.orderByChild("lastUpdatedTimeStamp/timestamp");
                    listAdapter = new FirebaseListAdapter<ChatModel>(getActivity(), ChatModel.class, R.layout.chat_list_view, query) {
                        @Override
                        protected void populateView(View v, ChatModel model, int position) {
                            TextView nameText = (TextView) v.findViewById(R.id.chat_list_userName);
                            TextView timeText = (TextView) v.findViewById(R.id.chat_list_time);
                            TextView messageText = (TextView) v.findViewById(R.id.chat_list_message);
                            ImageView userProfileImage = (ImageView) v.findViewById(R.id.chat_list_user_profile);
                            String listId=listAdapter.getRef(position).getKey();
                            String fileName=listId+".png";
                            Bitmap profilePic=getThumbnail(fileName);
                            if(profilePic!=null)
                                userProfileImage.setImageBitmap(profilePic);
                            else
                                userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_dummy_profile_primary));
                            nameText.setText(model.getUserName());
                            String time;
                            messageText.setText(model.getLastMessage());
                            HashMap<String, Object> obj = model.getLastUpdatedTimeStamp();
                            long milli = (long) obj.get(Constants.CONSTANT_TIMESTAMP);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(0-milli);
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
                } else {
                    noChatCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    private void downloadUserProfileImages() {
        chatRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_CHATS);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    final String fileName = key + ".png";
                    profileRef = storageReference.child(key).child(Constants.CONSTANT_PROFILE_PIC);
                    profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap1 != null)
                                saveImageToInternalStorage(bitmap1, fileName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public Bitmap getThumbnail(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = getActivity().getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e(" internal storage Error", ex.getMessage());
        }
        return thumbnail;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_chat_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
