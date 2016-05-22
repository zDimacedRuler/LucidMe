package com.example.amankumar.lucidme.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class DreamDetailActivity extends AppCompatActivity {
    //Display ViewGroups
    TextView dateOfDreamText, lucidTechniqueText;
    TextView dreamSignText;
    TextView titleText, dreamText, notesText;
    ImageView lucidImage;
    CardView dreamSignCard;
    CardView notesCardView;
    //associated strings
    String lucidTechnique, lucidState, title, dream, notes;
    ArrayList<String> dreamSigns;
    GregorianCalendar gregorianCalendar;
    Toolbar toolbar;
    String listId;
    String encodedEmail;
    SharedPreferences sp;
    Firebase dreamDetailRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_detail);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Read your dream");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        listId = getIntent().getStringExtra("listId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        dreamDetailRef = new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail).child(Constants.LOCATION_DREAMS).child(listId);
        dreamDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dream dreamModel = dataSnapshot.getValue(Dream.class);
                //date
                gregorianCalendar = dreamModel.getCalendar();
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
                        lucidTechniqueText.setVisibility(View.VISIBLE);
                        lucidTechniqueText.setText(lucidTechnique);
                    }else{
                        lucidTechniqueText.setVisibility(View.GONE);
                    }
                } else {
                    lucidImage.setVisibility(View.GONE);
                    lucidTechniqueText.setVisibility(View.GONE);
                }
                if (dataSnapshot.child("dreamSigns").exists()) {
                    dreamSignCard.setVisibility(View.VISIBLE);
                    dreamSignText.setText("");
                    HashMap<String, Object> obj;
                    obj = dreamModel.getDreamSigns();
                    dreamSigns = (ArrayList<String>) obj.get(Constants.CONSTANT_DREAMSIGNS);
                    for (String sign : dreamSigns) {
                        String text = "#" + sign + "  ";
                        dreamSignText.append(text);
                    }
                } else {
                    dreamSignCard.setVisibility(View.GONE);
                }
                title = dreamModel.getTitleDream();
                titleText.setText(title);
                dream = dreamModel.getDream();
                dreamText.setText(dream);
                notes = dreamModel.getAdditionalNotes();
                if (!notes.equals("")) {
                    notesCardView.setVisibility(View.VISIBLE);
                    notesText.setText(notes);
                } else
                    notesCardView.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        encodedEmail = sp.getString(Constants.CURRENT_USER, "");
        dateOfDreamText = (TextView) findViewById(R.id.DD_date_of_dream);
        lucidImage = (ImageView) findViewById(R.id.DD_lucid_imageView);
        lucidTechniqueText = (TextView) findViewById(R.id.DD_lucid_techniqueText);
        dreamSignText = (TextView) findViewById(R.id.DD_dreamSign_Text);
        titleText = (TextView) findViewById(R.id.DD_title_Text);
        dreamText = (TextView) findViewById(R.id.DD_dream_Text);
        notesText = (TextView) findViewById(R.id.DD_notes_Text);
        dreamSignCard = (CardView) findViewById(R.id.DD_dreamSign_card_view);
        notesCardView = (CardView) findViewById(R.id.DD_notes_card_view);
        dreamSigns = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dream_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==android.R.id.home){
            finish();
        }
        if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Delete dream record?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Firebase deleteRef = new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail).child(Constants.LOCATION_DREAMS).child(listId);
                    deleteRef.setValue(null);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void EditDreamButtonHandler(View view) {
        Intent intent = new Intent(this, DreamActivity.class);
        intent.putExtra("caller", "DreamDetail");
        intent.putExtra("listId", listId);
        startActivityForResult(intent, 2);
    }
}
