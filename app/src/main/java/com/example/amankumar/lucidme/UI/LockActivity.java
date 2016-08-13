package com.example.amankumar.lucidme.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amankumar.lucidme.Account.LoginActivity;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LockActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    SharedPreferences sp;
    String pin,enteredPin;
    EditText pinEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        mAuth=FirebaseAuth.getInstance();
        pin =sp.getString(Constants.PIN_PASSWORD,"0000");
        pinEdit= (EditText) findViewById(R.id.LA_pin_edit);
        pinEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(Constants.CURRENT_USER, null).apply();
                    spe.putString(Constants.CURRENT_USER_NAME, null).apply();
                    spe.putBoolean(Constants.PIN_STATE,false).apply();
                    spe.putString(Constants.PIN_PASSWORD,"0000").apply();
                    takeUserToLoginScreenOnUnAuth();
                }
            }
        };
        pinEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==4){
                    enteredPin=pinEdit.getText().toString();
                    if(pin.equals(enteredPin)){
                        SharedPreferences.Editor spe=sp.edit();
                        spe.putBoolean(Constants.BLOCK_STATE,false).apply();
                        Utils.lockAppStoreTime(LockActivity.this);
                        Intent intent=new Intent(LockActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        pinEdit.setText("");
                        Toast.makeText(LockActivity.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void forgotPinHandler(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Pin");
        builder.setMessage("Log out to reset pin?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
