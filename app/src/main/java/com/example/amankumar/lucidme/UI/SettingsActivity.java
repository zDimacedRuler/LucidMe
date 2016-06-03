package com.example.amankumar.lucidme.UI;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Services.WakeUpWithJournalService;
import com.example.amankumar.lucidme.Utils.Constants;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences sp;
    Switch disableAllSwitch, wakeUpWithJournalSwitch;
    Switch nightModeSwitch;
    LinearLayout toDisableView;
    Boolean disableAll,wakeUpWithJournalState,nightMode;
    static SharedPreferences.Editor spe;
    static Button fromTimePickerButton, toTimePickerButton;
    static Button sleepWithJournalButton, wakeUpWithJournalButton;
    AlarmManager alarmManager;
    Calendar wakeUpCalendar;
    PendingIntent pendingIntent;
    static int fromHour;
    static int fromMinute;
    static int toHour;
    static int toMinute;
    static int wakeHour;
    static int wakeMinute;
    static int sleepHour;
    static int sleepMinute;

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
    }

    private void settingsInit() {
        fromHour = sp.getInt(Constants.FROM_HOUR, 7);
        fromMinute = sp.getInt(Constants.FROM_MINUTE, 0);
        toHour = sp.getInt(Constants.TO_HOUR, 22);
        toMinute = sp.getInt(Constants.TO_MINUTE, 0);
        wakeHour = sp.getInt(Constants.WAKE_HOUR, 7);
        wakeMinute = sp.getInt(Constants.WAKE_MINUTE, 0);
        sleepHour = sp.getInt(Constants.SLEEP_HOUR, 23);
        sleepMinute = sp.getInt(Constants.SLEEP_MINUTE, 0);
        fromTimePickerButton = (Button) findViewById(R.id.S_from_time_picker);
        toTimePickerButton = (Button) findViewById(R.id.S_to_time_picker);
        sleepWithJournalButton = (Button) findViewById(R.id.S_sleep_with_journal);
        wakeUpWithJournalButton = (Button) findViewById(R.id.S_wake_up_with_journal);
        fromTimePickerButton.setText(showTime(fromHour, fromMinute));
        toTimePickerButton.setText(showTime(toHour, toMinute));
        wakeUpWithJournalButton.setText(showTime(wakeHour, wakeMinute));
        sleepWithJournalButton.setText(showTime(sleepHour, sleepMinute));
        toDisableView = (LinearLayout) findViewById(R.id.S_to_disable_view);
        nightModeSwitch= (Switch) findViewById(R.id.S_night_mode_switch);
        disableAllSwitch = (Switch) findViewById(R.id.S_disable_all_switch);
        wakeUpWithJournalSwitch = (Switch) findViewById(R.id.S_wake_up_with_journal_switch);
        disableAll = sp.getBoolean(Constants.DISABLE_ALL_STATE, true);
        nightMode=sp.getBoolean(Constants.NIGHT_MODE,false);
        wakeUpWithJournalState=sp.getBoolean(Constants.WAKE_UP_JOURNAL_STATE,true);
        nightModeSwitch.setChecked(nightMode);
        disableAllSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                disableAll = isChecked;
                spe.putBoolean(Constants.DISABLE_ALL_STATE, isChecked).apply();
                if (isChecked){
                    toDisableView.setVisibility(View.GONE);
                    wakeUpWIthJournalInit();
                }
                else{
                    toDisableView.setVisibility(View.VISIBLE);
                    wakeUpWIthJournalInit();
                }
            }
        });
        disableAllSwitch.setChecked(disableAll);
    }

    private void wakeUpWIthJournalInit() {
        if (!disableAll) {
            Intent myIntent=new Intent(this, WakeUpWithJournalService.class);
            alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent=PendingIntent.getService(this,0,myIntent,0);
            wakeUpCalendar=Calendar.getInstance();
            wakeUpCalendar.setTimeInMillis(System.currentTimeMillis());
            wakeUpCalendar.set(Calendar.HOUR_OF_DAY,wakeHour);
            wakeUpCalendar.set(Calendar.MINUTE,wakeMinute);
            wakeUpWithJournalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    wakeUpWithJournalState=isChecked;
                    spe.putBoolean(Constants.WAKE_UP_JOURNAL_STATE,wakeUpWithJournalState).apply();
                    if(isChecked){
                        Toast.makeText(SettingsActivity.this, "Alarm made", Toast.LENGTH_SHORT).show();
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,wakeUpCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

                    }else{
                        Toast.makeText(SettingsActivity.this, "Alarm canceled", Toast.LENGTH_SHORT).show();
                        alarmManager.cancel(pendingIntent);
                    }
                }
            });
            wakeUpWithJournalSwitch.setChecked(wakeUpWithJournalState);
        }
    }

    public void SleepWithJournalPicker(View view) {
        DialogFragment dialogFragment = new SleepWithJournalPicker();
        dialogFragment.show(getSupportFragmentManager(), "SleepWithJournal");
    }

    public void WakeUpWithJournalHandler(View view) {
        DialogFragment dialogFragment = new WakeUpWithJournalPicker();
        dialogFragment.show(getSupportFragmentManager(), "WakeUpWithJournal");
    }

    public void CheckChangedListener(View view) {
        nightMode=sp.getBoolean(Constants.NIGHT_MODE,false);
        if(nightMode==Boolean.TRUE){
            spe.putBoolean(Constants.NIGHT_MODE,false).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightModeSwitch.setChecked(false);
        }else{
            spe.putBoolean(Constants.NIGHT_MODE,true).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightModeSwitch.setChecked(true);
        }
        recreate();
    }

    public static class SleepWithJournalPicker extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, sleepHour, sleepMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            sleepHour = hourOfDay;
            sleepMinute = min;
            sleepWithJournalButton.setText(showTime(sleepHour, sleepMinute));
            spe.putInt(Constants.SLEEP_HOUR, sleepHour).apply();
            spe.putInt(Constants.SLEEP_MINUTE, sleepMinute).apply();
        }
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
            wakeUpWithJournalButton.setText(showTime(wakeHour, wakeMinute));
            spe.putInt(Constants.WAKE_HOUR, wakeHour).apply();
            spe.putInt(Constants.WAKE_MINUTE, wakeMinute).apply();
        }
    }

    public static class TimePickerFragmentFrom extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, fromHour, fromMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            fromHour = hourOfDay;
            fromMinute = min;
            fromTimePickerButton.setText(showTime(fromHour, fromMinute));
            spe.putInt(Constants.FROM_HOUR, fromHour).apply();
            spe.putInt(Constants.FROM_MINUTE, fromMinute).apply();
        }
    }

    public static class TimePickerFragmentTo extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, toHour, toMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            toHour = hourOfDay;
            toMinute = min;
            toTimePickerButton.setText(showTime(toHour, toMinute));
            spe.putInt(Constants.TO_HOUR, toHour).apply();
            spe.putInt(Constants.TO_MINUTE, toMinute).apply();
        }
    }

    public void showTimePickerFrom(View view) {
        DialogFragment newFragment = new TimePickerFragmentFrom();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showTimePickerTo(View view) {
        DialogFragment newFragment = new TimePickerFragmentTo();
        newFragment.show(getSupportFragmentManager(), "timePicker");
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
                Intent intent=new Intent(this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
