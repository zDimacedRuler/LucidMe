package com.example.amankumar.lucidme.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.HomeActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    String email, password, userName, encodedEmail;
    EditText passwordEdit, userNameEdit,emailEdit;
    ProgressDialog mProgressDialog;
    FirebaseDatabase ref;
    DatabaseReference userRef, userDetailRef;
    DatabaseReference dreamSignRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    createUserInFireBaseHelper();
                    login();
                }
            }
        };
    }


    private void init() {
        ref = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        emailEdit= (EditText) findViewById(R.id.email_SignUpEditText);
        passwordEdit = (EditText) findViewById(R.id.password_SignUpEditText);
        userNameEdit = (EditText) findViewById(R.id.username_SignUpEditText);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setMessage("Creating Account");
        mProgressDialog.setCancelable(false);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null)
            mAuth.removeAuthStateListener(authStateListener);
    }

    public void SignUpUserHandler(View view) {
        email=emailEdit.getText().toString();
        password = String.valueOf(passwordEdit.getText());
        userName = String.valueOf(userNameEdit.getText());
        boolean validPassword = isPasswordValid(password);
        boolean validUserName = isUserNameValid(userName);
        boolean validEmail = isEmailValid(email);
        if (!validPassword || !validUserName || !validEmail)
            return;
        mProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressDialog.dismiss();
                SharedPreferences.Editor spe = sp.edit();
                encodedEmail = Utils.encodeEmail(email);
                spe.putString(Constants.CURRENT_USER, encodedEmail).apply();
                if (!task.isSuccessful()) {
                    showErrorToast(task.getException().getMessage());
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            emailEdit.setError("Invalid Email");
            return false;
        }
        return true;
    }

    private void login() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void createUserInFireBaseHelper() {
        encodedEmail = Utils.encodeEmail(email);
        userRef = ref.getReference().child(Constants.LOCATION_USERS).child(encodedEmail);
        userDetailRef = ref.getReference().child(Constants.LOCATION_USER_DETAILS);
        dreamSignRef = ref.getReference().child(Constants.LOCATION_DREAMS_SIGNS);
        userRef.child(Constants.LOCATION_USERNAME).setValue(userName);
        userDetailRef.child(encodedEmail).setValue(userName);
        dreamSignRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference userDreamRef = FirebaseDatabase.getInstance().getReference().child(Constants.LOCATION_USERS).child(encodedEmail).child(Constants.LOCATION_DREAMS_SIGNS);
                userDreamRef.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean isUserNameValid(String userName) {
        if (userName.length() < 6) {
            userNameEdit.setError("Username should have more than 6 characters");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            passwordEdit.setError("Password should not be empty and have more than 6 characters");
            return false;
        }
        return true;
    }
}
