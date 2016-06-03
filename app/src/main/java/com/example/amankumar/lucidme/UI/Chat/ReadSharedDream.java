package com.example.amankumar.lucidme.UI.Chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReadSharedDream extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    String currentUser,sharedUser,dreamId,userName;
    FirebaseDatabase ref;
    DatabaseReference sharedDreamRef;
    TextView dateOfDreamText, lucidTechniqueText;
    TextView dreamSignText;
    TextView titleText, dreamText,labelText;
    ImageView lucidImage,labelImage;
    CardView dreamSignCard;
    CardView techniqueCardView;
    //associated strings
    String lucidTechnique, lucidState, title, dream;
    ArrayList<String> dreamSigns;
    GregorianCalendar gregorianCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_shared_dream);
        userName=getIntent().getStringExtra("userName");
        toolbar= (Toolbar) findViewById(R.id.RSD_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shared by "+userName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        sharedDreamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dream dreamModel=dataSnapshot.getValue(Dream.class);
                long milliseconds=dreamModel.getDateOfDream();
                gregorianCalendar.setTimeInMillis(milliseconds);
                int monthOfYear = gregorianCalendar.get(Calendar.MONTH);
                int dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
                int dayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                String day = Utils.getDay(dayOfWeek);
                String month = Utils.getMonth(monthOfYear);
                int year = gregorianCalendar.get(Calendar.YEAR);
                String date = day + "," + dayOfMonth + " " + month + " " + year;
                dateOfDreamText.setText(date);
                //lucid checkBox
                lucidState = dreamModel.getLucid();
                if (lucidState.equals("True")) {
                    lucidImage.setVisibility(View.VISIBLE);
                    lucidTechnique = dreamModel.getLucidTechnique();
                    if (!lucidTechnique.equals("")) {
                        techniqueCardView.setVisibility(View.VISIBLE);
                        lucidTechniqueText.setText(lucidTechnique);
                    }else{
                        techniqueCardView.setVisibility(View.GONE);
                    }
                } else {
                    lucidImage.setVisibility(View.GONE);
                    techniqueCardView.setVisibility(View.GONE);
                }
                if (dataSnapshot.child(Constants.CONSTANT_USERDREAMSIGNS).exists()) {
                    labelImage.setVisibility(View.VISIBLE);
                    labelText.setVisibility(View.VISIBLE);
                    dreamSignCard.setVisibility(View.VISIBLE);
                    dreamSignText.setText("");
                    HashMap<String, Object> obj;
                    obj = dreamModel.getUserDreamSigns();
                    dreamSigns = (ArrayList<String>) obj.get(Constants.CONSTANT_USERDREAMSIGNS);
                    labelText.setText(String.valueOf(dreamSigns.size()));
                    for (String sign : dreamSigns) {
                        String text = "#" + sign + "  ";
                        dreamSignText.append(text);
                    }
                } else {
                    labelImage.setVisibility(View.GONE);
                    labelText.setVisibility(View.GONE);
                    dreamSignCard.setVisibility(View.GONE);
                }
                title = dreamModel.getTitleDream();
                titleText.setText(title);
                dream = dreamModel.getDream();
                dreamText.setText(dream);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        currentUser=sp.getString(Constants.CURRENT_USER,"");
        sharedUser=getIntent().getStringExtra("sharedUser");
        dreamId=getIntent().getStringExtra("dreamId");
        ref=FirebaseDatabase.getInstance();
        sharedDreamRef=ref.getReference().child(Constants.LOCATION_USERS).child(currentUser).child(Constants.LOCATION_SHARED_DREAM).child(sharedUser).child(dreamId);
        dateOfDreamText= (TextView) findViewById(R.id.RSD_date_of_dream);
        lucidTechniqueText= (TextView) findViewById(R.id.RSD_lucid_techniqueText);
        dreamSignText= (TextView) findViewById(R.id.RSD_dreamSign_Text);
        titleText= (TextView) findViewById(R.id.RSD_title_Text);
        dreamText= (TextView) findViewById(R.id.RSD_dream_Text);
        labelText= (TextView) findViewById(R.id.RSD_textLabel);
        lucidImage= (ImageView) findViewById(R.id.RSD_lucid_imageView);
        labelImage= (ImageView) findViewById(R.id.RSD_imageLabel);
        dreamSignCard= (CardView) findViewById(R.id.RSD_dreamSign_card_view);
        techniqueCardView= (CardView) findViewById(R.id.RSD_lucid_technique_card_view);
        gregorianCalendar= (GregorianCalendar)Calendar.getInstance();
    }
}
