package com.example.amankumar.lucidme.UI;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    Switch wakeUpWithJournalSwitch;
    Switch nightModeSwitch;
    Spinner sortBySpinner;
    Boolean  wakeUpWithJournalState, nightMode;
    static SharedPreferences.Editor spe;
    static Button wakeUpWithJournalButton;
    static AlarmManager alarmManager;
    int sortState;
    static int wakeHour;
    static int wakeMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spe = sp.edit();
        settingsInit();
        sortInit();
    }

    private void sortInit() {
        sortState = sp.getInt(Constants.SORT_STATE, 0);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter);
        sortBySpinner.setSelection(sortState);
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortState = position;
                spe.putInt(Constants.SORT_STATE, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void CheckChangedListener(View view) {
        nightMode = sp.getBoolean(Constants.NIGHT_MODE, false);
        if (nightMode == Boolean.TRUE) {
            spe.putBoolean(Constants.NIGHT_MODE, false).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightModeSwitch.setChecked(false);
        } else {
            spe.putBoolean(Constants.NIGHT_MODE, true).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightModeSwitch.setChecked(true);
        }
        recreate();
    }

    private void settingsInit() {
        wakeUpWithJournalButton = (Button) findViewById(R.id.S_wake_up_with_journal);
        wakeUpWithJournalButton.setText(showTime(wakeHour, wakeMinute));
        nightModeSwitch = (Switch) findViewById(R.id.S_night_mode_switch);
        wakeUpWithJournalSwitch = (Switch) findViewById(R.id.S_wake_up_with_journal_switch);
        sortBySpinner = (Spinner) findViewById(R.id.S_sort_by_spinner);
        nightMode = sp.getBoolean(Constants.NIGHT_MODE, false);
        wakeUpWithJournalState = sp.getBoolean(Constants.WAKE_UP_JOURNAL_STATE, true);
        nightModeSwitch.setChecked(nightMode);
    }

    public void WakeUpWithJournalHandler(View view) {
        DialogFragment dialogFragment = new WakeUpWithJournalPicker();
        dialogFragment.show(getSupportFragmentManager(), "WakeUpWithJournal");
    }


    public static class WakeUpWithJournalPicker extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, wakeHour, wakeMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            wakeHour = hourOfDay;
            wakeMinute = min;
            spe.putInt(Constants.WAKE_HOUR, wakeHour).apply();
            spe.putInt(Constants.WAKE_MINUTE, wakeMinute).apply();
        }
    }
    
    private static String showTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();
        return aTime;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
