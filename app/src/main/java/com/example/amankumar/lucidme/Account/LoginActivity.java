package com.example.amankumar.lucidme.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.HomeActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class LoginActivity extends AppCompatActivity {
    Firebase ref;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    EditText emailEdit, passwordEdit;
    ProgressDialog mProgressDialog;
    String email,password,encodedEmail,currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ref = new Firebase(Constants.FIREBASE_URL);
        init();
        currentUser=sp.getString(Constants.CURRENT_USER,null);
        if(currentUser!=null)
            takeUserToDreamJournalActivity();
    }

    private void init() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        spe = sp.edit();
        emailEdit = (EditText) findViewById(R.id.email_EditText);
        passwordEdit = (EditText) findViewById(R.id.password_EditText);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setMessage("Connecting to your dreams");
        mProgressDialog.setCancelable(true);
    }

    public void SignUpButtonHandler(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String signUpEmail = sp.getString(Constants.SIGNUP_EMAIL, null);
        if (signUpEmail != null) {
            emailEdit.setText(signUpEmail);
            spe.putString(Constants.SIGNUP_EMAIL, null).apply();
        }
    }

    public void LoginButtonHandler(View view) {
        email=emailEdit.getText().toString().toLowerCase();
        password=passwordEdit.getText().toString();
        encodedEmail= Utils.encodeEmail(email);
        if (email.equals("")) {
            emailEdit.setError("Email cannot be empty");
            return;
        }
        if (password.equals("")){
            passwordEdit.setError("Field cannot be empty");
            return;
        }
        mProgressDialog.show();
        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                mProgressDialog.dismiss();
                if(authData!=null){
                    spe.putString(Constants.CURRENT_USER,encodedEmail).apply();
                    takeUserToDreamJournalActivity();
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mProgressDialog.dismiss();
                switch (firebaseError.getCode()) {
                    case FirebaseError.INVALID_EMAIL:
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        emailEdit.setError(getString(R.string.error_message_email_issue));
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        passwordEdit.setError(firebaseError.getMessage());
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                        break;
                    default:
                        showErrorToast(firebaseError.toString());
                }
            }
        });
    }

    private void takeUserToDreamJournalActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void showErrorToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
