package com.example.amankumar.lucidme.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.amankumar.lucidme.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {
    FirebaseDatabase ref;
    DatabaseReference testRef;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ref=FirebaseDatabase.getInstance();
        testRef=ref.getReference().child("test");
    }
}
