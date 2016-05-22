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
import android.widget.ListView;

import com.example.amankumar.lucidme.Adapter.SelectedDreamSignAdapter;
import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.Model.UsedDreamSignModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class SelectedDreamSignActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    String currentUser, listId;
    Firebase selectedDreamSignRef, dreamRef;
    ListView selectedDreamSignListView;
    SelectedDreamSignAdapter dreamSignAdapter;
    ArrayList<Dream> selectedDreams;
    ValueEventListener valueEventListener;
    CardView noDreamCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_dream_sign);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        selectedDreamSignListView = (ListView) findViewById(R.id.SDS_list_view);
        noDreamCardView = (CardView) findViewById(R.id.SDS_no_dream_card_view);
        listId = getIntent().getStringExtra("listId");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dreamSignAdapter != null)
            dreamSignAdapter.clear();
    }

    public void onItemClick(int mPosition, String mListId) {
        Intent intent = new Intent(SelectedDreamSignActivity.this, DreamDetailActivity.class);
        intent.putExtra("listId", mListId);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedDreams = new ArrayList<>();
        dreamRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser).child(Constants.LOCATION_DREAMS);
        selectedDreamSignRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser).child(Constants.LOCATION_USED_DREAMS_SIGNS).child(listId);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UsedDreamSignModel model = dataSnapshot.getValue(UsedDreamSignModel.class);
                final ArrayList<Object> list = model.getDREAMSIGNS();
                getSupportActionBar().setTitle(list.get(0).toString());
                list.remove(0);
                dreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (list.contains(child.getKey())) {
                                Dream dream = child.getValue(Dream.class);
                                selectedDreams.add(dream);
                            }
                        }
                        if (selectedDreams.size() == 0) {
                            noDreamCardView.setVisibility(View.VISIBLE);
                        } else {
                            noDreamCardView.setVisibility(View.GONE);
                        }
                        dreamSignAdapter = new SelectedDreamSignAdapter(SelectedDreamSignActivity.this, R.layout.listview, selectedDreams, list);
                        selectedDreamSignListView.setAdapter(dreamSignAdapter);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        selectedDreamSignRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedDreamSignRef.removeEventListener(valueEventListener);
    }
}
