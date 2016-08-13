package com.example.amankumar.lucidme.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AnalysisActivity extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseDatabase ref;
    DatabaseReference dreamRef, dreamSignRef;
    SharedPreferences sp;
    TextView nDreamText, nDreamSignText, nLucidText, nWeekText, nMonthText, percentageText;
    String currentUser;
    Calendar calendar;
    int noOfDreams;
    int noOfLucidDreams;
    long noOfDreamSigns;
    int dreamsThisWeek;
    int dreamThisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        toolbar = (Toolbar) findViewById(R.id.AnA_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Analysis");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        dreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    noOfDreams++;
                    Dream dream = snapshot.getValue(Dream.class);
                    String lucid = dream.getLucid();
                    long milli = dream.getDateOfDream();
                    Calendar dateOfDream = Calendar.getInstance();
                    dateOfDream.setTimeInMillis(milli);
                    if (lucid.equals("True"))
                        noOfLucidDreams++;
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    int week = calendar.get(Calendar.WEEK_OF_MONTH);
                    if (dateOfDream.get(Calendar.YEAR) == year && dateOfDream.get(Calendar.MONTH) == month)
                        dreamThisMonth++;
                    if (dateOfDream.get(Calendar.YEAR) == year && dateOfDream.get(Calendar.MONTH) == month && dateOfDream.get(Calendar.WEEK_OF_MONTH) == week)
                        dreamsThisWeek++;

                }
                double percentage;
                if (noOfDreams > 0)
                     percentage = (double) noOfLucidDreams / noOfDreams * 100;
                else
                    percentage=0;
                String percentText = String.format("%.2f", percentage);
                String lucidText = percentText + "%";
                nDreamText.setText(String.valueOf(noOfDreams));
                nLucidText.setText(String.valueOf(noOfLucidDreams));
                percentageText.setText(lucidText);
                nWeekText.setText(String.valueOf(dreamsThisWeek));
                nMonthText.setText(String.valueOf(dreamThisMonth));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dreamSignRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfDreamSigns = dataSnapshot.getChildrenCount();
                nDreamSignText.setText(String.valueOf(noOfDreamSigns));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        noOfLucidDreams = 0;
        noOfDreams = 0;
        noOfDreamSigns = 0;
        ref = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        dreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS);
        dreamSignRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_DREAMS_SIGNS);
        nDreamText = (TextView) findViewById(R.id.AnA_no_of_dreams_text);
        nDreamSignText = (TextView) findViewById(R.id.AnA_no_of_dream_sign_text);
        nLucidText = (TextView) findViewById(R.id.AnA_no_of_lucid_dreams_text);
        nWeekText = (TextView) findViewById(R.id.AnA_week_text);
        nMonthText = (TextView) findViewById(R.id.AnA_month_text);
        percentageText = (TextView) findViewById(R.id.AnA_lucid_percentage_text);
    }
}
