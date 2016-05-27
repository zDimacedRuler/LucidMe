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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectedDreamSignActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    String currentUser, listId;
    FirebaseDatabase ref;
    DatabaseReference selectedDreamSignRef, dreamRef;
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
        ref = FirebaseDatabase.getInstance();
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
        dreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS);
        selectedDreamSignRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_USED_DREAMS_SIGNS).child(listId);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UsedDreamSignModel model = dataSnapshot.getValue(UsedDreamSignModel.class);
                final ArrayList<String> list = model.getDREAMSIGNS();
                getSupportActionBar().setTitle(list.get(0));
                list.remove(0);
                dreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> presentListId = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).equals(child.getKey())) {
                                    Dream dream = child.getValue(Dream.class);
                                    selectedDreams.add(dream);
                                    presentListId.add(list.get(i));
                                }
                            }
                        }

                        if (selectedDreams.size() == 0) {
                            noDreamCardView.setVisibility(View.VISIBLE);
                        } else {
                            noDreamCardView.setVisibility(View.GONE);
                        }
                        dreamSignAdapter = new SelectedDreamSignAdapter(SelectedDreamSignActivity.this, R.layout.listview, selectedDreams, presentListId);
                        selectedDreamSignListView.setAdapter(dreamSignAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
