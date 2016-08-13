package com.example.amankumar.lucidme.UI.Search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.example.amankumar.lucidme.Adapter.SearchableAdapter;
import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.DreamDetailActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    EditText searchEdit;
    ListView searchList;
    private Toolbar toolbar;
    SharedPreferences sp;
    String currentUser;
    FirebaseDatabase ref;
    DatabaseReference dreamRef;
    Query query;
    ArrayList<Dream> resultList;
    ArrayList<String> resultListId;
    SearchableAdapter resultAdapter;
    CharSequence str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (str != null)
            search(str);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                str = s;
                search(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void search(final CharSequence s) {
        if (s.length() == 0) {
            searchList.setAdapter(null);
            return;
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultList.clear();
                resultListId.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dream dream = snapshot.getValue(Dream.class);
                    String dreamContent=dream.getDream().toLowerCase();
                    String dreamTitle=dream.getTitleDream().toLowerCase();
                    String dreamNotes=dream.getAdditionalNotes().toLowerCase();
                    String searchString=s.toString();
                    searchString=searchString.toLowerCase();
                    if (dreamContent.contains(searchString)||dreamTitle.contains(searchString)||dreamNotes.contains(searchString)) {
                        resultList.add(dream);
                        resultListId.add(snapshot.getKey());
                    }
                }
                resultAdapter = new SearchableAdapter(SearchableActivity.this, R.layout.listview, resultList, resultListId);
                searchList.setAdapter(resultAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putCharSequence("SearchString", str);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            str = savedInstanceState.getCharSequence("SearchString");
        }
    }

    public void onItemClick(int mPosition, String mListId) {
        Intent intent = new Intent(SearchableActivity.this, DreamDetailActivity.class);
        intent.putExtra("listId", mListId);
        startActivity(intent);

    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        searchEdit = (EditText) findViewById(R.id.SA_searchEdit);
        searchList = (ListView) findViewById(R.id.SA_searchList);
        ref = FirebaseDatabase.getInstance();
        dreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS);
        query = dreamRef.orderByChild("revDateOfDream");
        resultList = new ArrayList<>();
        resultListId = new ArrayList<>();
    }
}
