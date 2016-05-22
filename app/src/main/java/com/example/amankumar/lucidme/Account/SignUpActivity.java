package com.example.amankumar.lucidme.Account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    ArrayList<String> emails;
    Spinner emailSpinner;
    String email,password,userName,encodedEmail;
    ArrayAdapter<String> adapter;
    EditText passwordEdit,userNameEdit;
    ProgressDialog mProgressDialog;
    Firebase ref,userRef,dreamSignRef;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        try{
            Account[] accounts= AccountManager.get(this).getAccountsByType("com.google");
            for(Account account : accounts){
                Log.e("SignUpActivity","Account:"+account.name);
                emails.add(account.name);
            }
        }
        catch (Exception e){
            Log.e("SignUpActivity","Exception:"+e);
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,emails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailSpinner.setAdapter(adapter);
        emailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                email=adapter.getItem(position);
                Toast.makeText(SignUpActivity.this,email,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {
        ref=new Firebase(Constants.FIREBASE_URL);
        emailSpinner= (Spinner) findViewById(R.id.email_spinner);
        emails=new ArrayList<>();
        passwordEdit= (EditText) findViewById(R.id.password_SignUpEditText);
        userNameEdit= (EditText) findViewById(R.id.username_SignUpEditText);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setMessage("Creating Account");
        mProgressDialog.setCancelable(false);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void SignUpUserHandler(View view) {
        password= String.valueOf(passwordEdit.getText());
        userName= String.valueOf(userNameEdit.getText());
        boolean validPassword=isPasswordValid(password);
        boolean validUserName=isUserNameValid(userName);
        if(!validPassword || !validUserName)
            return;
        mProgressDialog.show();
        ref.createUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();
                createUserInFireBaseHelper();
                SharedPreferences.Editor spe=sp.edit();
                spe.putString(Constants.SIGNUP_EMAIL,email).apply();
                login();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                mProgressDialog.dismiss();
                showErrorToast(firebaseError.getMessage());
            }
        });
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void createUserInFireBaseHelper() {
        encodedEmail= Utils.encodeEmail(email);
        userRef=new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail);
        userRef.child(Constants.LOCATION_USERNAME).setValue(userName);
        dreamSignRef=new Firebase(Constants.FIREBASE_URL).child(Constants.LOCATION_DREAMS_SIGNS);
        dreamSignRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase userDreamRef=new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail).child(Constants.LOCATION_DREAMS_SIGNS);
                userDreamRef.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean isUserNameValid(String userName) {
        if (userName.length()<6) {
            userNameEdit.setError("Username should have more than 6 characters");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if(password.length()<6){
            passwordEdit.setError("Password should not be empty and have more than 6 characters");
            return false;
        }
        return true;
    }
}
