package com.example.amankumar.lucidme.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.Model.UsedDreamSignModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DreamSignActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    FirebaseDatabase ref;
    DatabaseReference userRef, dreamRef, userDreamRef, usedDreamSignRef, checkRef;
    String currentUser;
    ValueEventListener valueEventListener;
    ListView dreamSignListView;
    ArrayList<String> dreamSigns;
    FirebaseListAdapter<UsedDreamSignModel> usedDreamSignModelListAdapter;
    CardView noDreamSignCard;
    Map<String, HashMap<String, ArrayList<String>>> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_sign);
        toolbar = (Toolbar) findViewById(R.id.DS_app_bar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("DreamSigns");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        userRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
//        userRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser);
        init();
    }

    private void init() {
        dreamSignListView = (ListView) findViewById(R.id.DS_list_view);
        noDreamSignCard = (CardView) findViewById(R.id.DS_no_dream_card_view);
        dreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
        checkRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
        /*dreamRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser);
        checkRef=new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constants.LOCATION_DREAMS).exists()) {
                    userDreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS);
                    map = new HashMap<>();
                    userDreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.child(Constants.CONSTANT_USERDREAMSIGNS).exists()) {
                                    Dream dreamModel = child.getValue(Dream.class);
                                    HashMap<String, Object> obj;
                                    obj = dreamModel.getUserDreamSigns();
                                    dreamSigns = (ArrayList<String>) obj.get(Constants.CONSTANT_USERDREAMSIGNS);
                                    for (String s : dreamSigns) {
                                        HashMap<String, ArrayList<String>> listMap = map.get(s);
                                        if (listMap == null) {
                                            listMap = new HashMap<>();
                                            ArrayList<String> list = new ArrayList<String>();
                                            list.add(s);
                                            list.add(child.getKey());
                                            listMap.put(Constants.CONSTANT_USERDREAMSIGNS, list);
                                        } else {
                                            ArrayList<String> list = listMap.get(Constants.CONSTANT_USERDREAMSIGNS);
                                            list.add(child.getKey());
                                            listMap.put(Constants.CONSTANT_USERDREAMSIGNS, list);
                                        }
                                        map.put(s, listMap);
                                    }
                                }
                            }
                            usedDreamSignRef=ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_USED_DREAMS_SIGNS);
                            usedDreamSignRef.setValue(map);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    checkRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(Constants.LOCATION_USED_DREAMS_SIGNS).exists()) {
                                noDreamSignCard.setVisibility(View.GONE);
                            } else {
                                noDreamSignCard.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    usedDreamSignRef=ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_USED_DREAMS_SIGNS);
                    usedDreamSignModelListAdapter=new FirebaseListAdapter<UsedDreamSignModel>(DreamSignActivity.this, UsedDreamSignModel.class, R.layout.dream_sign_list_view, usedDreamSignRef) {
                        @Override
                        protected void populateView(View v, UsedDreamSignModel model, int position) {
                            ArrayList<String> list = model.getDREAMSIGNS();
                            TextView dreamSignView = (TextView) v.findViewById(R.id.DS_LV_sign_text);
                            dreamSignView.setText(list.get(0));
                            TextView noOfDreams = (TextView) v.findViewById(R.id.DS_LV_no_text);
                            String noOfDreamsText = "Dreams:" + (list.size() - 1);
                            noOfDreams.setText(noOfDreamsText);
                        }
                    };
                    dreamSignListView.setAdapter(usedDreamSignModelListAdapter);
                    dreamSignListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String listId = usedDreamSignModelListAdapter.getRef(position).getKey();
                            Intent intent = new Intent(DreamSignActivity.this, SelectedDreamSignActivity.class);
                            intent.putExtra("listId", listId);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    noDreamSignCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dreamRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dreamRef.removeEventListener(valueEventListener);
        if (usedDreamSignModelListAdapter != null)
            usedDreamSignModelListAdapter.cleanup();
        dreamSignListView.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userRef.child(Constants.LOCATION_USED_DREAMS_SIGNS).setValue(null);
    }
}
