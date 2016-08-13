package com.example.amankumar.lucidme.Application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.example.amankumar.lucidme.Utils.Constants;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by AmanKumar on 3/30/2016.
 */
public class LucidMeApp extends Application {
    SharedPreferences sp;
    Boolean nightMode;
    private static LucidMeApp instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        nightMode = sp.getBoolean(Constants.NIGHT_MODE, false);
        if (nightMode == Boolean.TRUE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
    public static LucidMeApp getInstance(){
        return instance;
    }
}
