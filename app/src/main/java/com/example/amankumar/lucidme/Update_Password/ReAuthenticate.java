package com.example.amankumar.lucidme.Update_Password;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReAuthenticate extends Fragment {
    EditText reAuthenticateEdit;
    Button authenticateButton;
    SharedPreferences sp;
    String currentUser, userEmail;
    buttonClickedListener clickedListener;
    public ReAuthenticate() {
        // Required empty public constructor
    }

    public static ReAuthenticate newInstance() {
        ReAuthenticate fragment = new ReAuthenticate();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_re_authenticate, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        userEmail = Utils.decodeEmail(currentUser);
        reAuthenticateEdit = (EditText) view.findViewById(R.id.RE_frag_pass_edit);
        authenticateButton = (Button) view.findViewById(R.id.RE_frag_button);
        authenticateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = reAuthenticateEdit.getText().toString();
                if (pass.equals("")) {
                    reAuthenticateEdit.setError("Field should not be empty");
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(userEmail, pass);
                if (user != null) {
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Authenticated", Toast.LENGTH_SHORT).show();
                                        if(clickedListener!=null){
                                            clickedListener.buttonClicked();
                                        }
                                    } else
                                        Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        return view;
    }
    public interface buttonClickedListener{
        void buttonClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            clickedListener = (buttonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentHostSignUp2Listener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        clickedListener = null;
    }
}
