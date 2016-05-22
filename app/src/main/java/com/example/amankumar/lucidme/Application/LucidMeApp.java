package com.example.amankumar.lucidme.Application;

import android.app.Application;

import com.firebase.client.Firebase;


/**
 * Created by AmanKumar on 3/30/2016.
 */
public class LucidMeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }

}
