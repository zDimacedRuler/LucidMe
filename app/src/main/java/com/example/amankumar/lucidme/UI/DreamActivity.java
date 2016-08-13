package com.example.amankumar.lucidme.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.DialogFragment.DreamSignDialog;
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
import java.util.Locale;

public class DreamActivity extends AppCompatActivity implements DreamSignDialog.EditNameDialogListener {
    Toolbar toolbar;
    //input controls
    EditText titleEdit, dreamEdit;
    TextView tagText;
    CheckBox lucidCheckBox;
    EditText lucidTechniqueEdit, addNotesEdit;
    //associated String
    String lucidTechnique, additionalNotes;
    String title, dream, encodedEmail, lucidState;
    long milliSeconds;
    FirebaseDatabase ref;
    DatabaseReference dreamDetailRef, addDreamRef;
    SharedPreferences sp;
    ArrayList<String> dreamSigns;
    CardView tagCardView;
    String caller, listId;
    static GregorianCalendar showCalendar;
    static GregorianCalendar sendCalendar;
    static TextView datePickerButton;
    private static final int RESULT_DONE = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    boolean pinState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);
        init();
        lucidCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lucidState = "True";
                    lucidTechniqueEdit.setVisibility(View.VISIBLE);
                } else {
                    lucidState = "False";
                    lucidTechniqueEdit.setVisibility(View.GONE);
                }
            }
        });
        if (encodedEmail == null) {
            Log.e("DreamActivity", "Error encoded email is null");
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_cancel_white);
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);
        dreamSigns = new ArrayList<>();
        titleEdit = (EditText) findViewById(R.id.title_EditText);
        dreamEdit = (EditText) findViewById(R.id.dream_EditText);
        lucidCheckBox = (CheckBox) findViewById(R.id.dream_lucid_checkBox);
        lucidTechniqueEdit = (EditText) findViewById(R.id.technique_dream_EditText);
        addNotesEdit = (EditText) findViewById(R.id.notes_dream_EditText);
        tagText = (TextView) findViewById(R.id.Dream_tagTextView);
        tagCardView = (CardView) findViewById(R.id.dream_tag_card_view);
        lucidState = "False";
        ref = FirebaseDatabase.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        encodedEmail = sp.getString(Constants.CURRENT_USER, null);
        showCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        sendCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        datePickerButton = (TextView) findViewById(R.id.DatePickerButton);
        caller = "";
        listId = "";
        showDate();
        caller = getIntent().getStringExtra("caller");
        if (caller != null && caller.equals("DreamDetail")) {
            listId = getIntent().getStringExtra("listId");
            dreamDetailRef = ref.getReference().child(Constants.LOCATION_USERS).child(encodedEmail).child(Constants.LOCATION_DREAMS).child(listId);
            dreamDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Dream dreamModel = dataSnapshot.getValue(Dream.class);
                    //date
                    milliSeconds = dreamModel.getDateOfDream();
                    sendCalendar.setTimeInMillis(milliSeconds);
                    showCalendar.setTimeInMillis(milliSeconds);
                    showDate();
                    //lucid checkBox
                    lucidState = dreamModel.getLucid();
                    if (lucidState.equals("True")) {
                        lucidCheckBox.setChecked(Boolean.TRUE);
                        lucidTechnique = dreamModel.getLucidTechnique();
                        lucidTechniqueEdit.setVisibility(View.VISIBLE);
                        lucidTechniqueEdit.setText(lucidTechnique);

                    }
                    if (dataSnapshot.child(Constants.CONSTANT_USERDREAMSIGNS).exists()) {
                        tagCardView.setVisibility(View.VISIBLE);
                        HashMap<String, Object> obj;
                        obj = dreamModel.getUserDreamSigns();
                        dreamSigns = (ArrayList<String>) obj.get(Constants.CONSTANT_USERDREAMSIGNS);
                        for (String sign : dreamSigns) {
                            String text = "#" + sign + "  ";
                            tagText.append(text);
                        }
                    }
                    title = dreamModel.getTitleDream();
                    titleEdit.setText(title);
                    dream = dreamModel.getDream();
                    dreamEdit.setText(dream);
                    additionalNotes = dreamModel.getAdditionalNotes();
                    addNotesEdit.setText(additionalNotes);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private static void showDate() {
        int monthOfYear = showCalendar.get(Calendar.MONTH);
        int dayOfMonth = showCalendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = showCalendar.get(Calendar.DAY_OF_WEEK);
        String day = Utils.getDay(dayOfWeek);
        String month = Utils.getMonth(monthOfYear);
        datePickerButton.setText(day + "," + dayOfMonth + " " + month);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            discardChanges();
        }
        if (id == R.id.action_dream_signs) {
            DialogFragment dialogFragment = DreamSignDialog.newInstance(dreamSigns);
            dialogFragment.show(getSupportFragmentManager(), "DreamSignDialogFragment");
        }
        if (id == R.id.action_audio_input) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void storeDream() {
        StringBuilder stringBuilder = new StringBuilder();
        title = String.valueOf(titleEdit.getText());
        stringBuilder.append(title);
        dream = String.valueOf(dreamEdit.getText());
        if (lucidState.equals("True"))
            lucidTechnique = lucidTechniqueEdit.getText().toString();
        else
            lucidTechnique = "";
        additionalNotes = addNotesEdit.getText().toString();
        HashMap<String, Object> obj = new HashMap<>();
        obj.put(Constants.CONSTANT_USERDREAMSIGNS, dreamSigns);
        if (title.equals("")) {
            if (dream.length() > 50)
                stringBuilder.append(dream.substring(0, 48));
            else
                stringBuilder.append(dream);
        }
        milliSeconds = sendCalendar.getTimeInMillis();
        Dream dreamEntry = new Dream(stringBuilder.toString(), dream, milliSeconds, obj, lucidState, lucidTechnique, additionalNotes);
        if (dream.equals("")) {
            setResult(2);
            finish();
        } else {
            if (caller != null && caller.equals("Home")) {
                addDreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(encodedEmail).child(Constants.LOCATION_DREAMS);
                DatabaseReference key = addDreamRef.push();
                key.setValue(dreamEntry);
                setResult(RESULT_DONE);
                finish();
            } else {
                addDreamRef = ref.getReference().child(Constants.LOCATION_USERS).child(encodedEmail).child(Constants.LOCATION_DREAMS).child(listId);
                addDreamRef.setValue(dreamEntry);
                finish();
            }
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onFinishedDialog(ArrayList<String> dreamSigns) {
        this.dreamSigns = dreamSigns;
        if (dreamSigns.isEmpty()) {
            tagCardView.setVisibility(View.GONE);
        } else {
            tagCardView.setVisibility(View.VISIBLE);
            tagText.setText("");
            for (String sign : dreamSigns) {
                String text = "#" + sign + "  ";
                tagText.append(text);
            }
        }
    }

    public void storeDreamHandler(View view) {
        storeDream();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = showCalendar.get(Calendar.YEAR);
            int month = showCalendar.get(Calendar.MONTH);
            int day = showCalendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);

        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sendCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            showCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            int dayOfWeek = showCalendar.get(Calendar.DAY_OF_WEEK);
            String month = Utils.getMonth(monthOfYear);
            String day = Utils.getDay(dayOfWeek);
            datePickerButton.setText(day + "," + dayOfMonth + " " + month);
        }
    }

    @Override
    public void onBackPressed() {
        storeDream();
        super.onBackPressed();
    }

    public void discardChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention Required");
        builder.setMessage("Changes are not saved.Save your changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storeDream();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    dreamEdit.append(result.get(0));
                }
                break;
            }

        }
    }
}
