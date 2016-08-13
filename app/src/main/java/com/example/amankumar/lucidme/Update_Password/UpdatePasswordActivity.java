package com.example.amankumar.lucidme.Update_Password;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.amankumar.lucidme.R;

public class UpdatePasswordActivity extends AppCompatActivity implements ReAuthenticate.buttonClickedListener{

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        toolbar = (Toolbar) findViewById(R.id.UP_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ReAuthenticate frag=ReAuthenticate.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.UP_container, frag).commit();
    }

    @Override
    public void buttonClicked() {
        Update frag = Update.newInstance();
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.UP_container, frag).commit();
    }
}
