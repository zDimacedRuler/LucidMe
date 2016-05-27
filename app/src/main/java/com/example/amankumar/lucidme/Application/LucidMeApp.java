package com.example.amankumar.lucidme.Application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by AmanKumar on 3/30/2016.
 */
public class LucidMeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
