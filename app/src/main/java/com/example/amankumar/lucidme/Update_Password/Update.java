package com.example.amankumar.lucidme.Update_Password;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amankumar.lucidme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Update extends Fragment {
    EditText newPassEdit, rePassEdit;
    Button updateButton;

    public Update() {
        // Required empty public constructor
    }

    public static Update newInstance() {
        Update fragment = new Update();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        newPassEdit = (EditText) view.findViewById(R.id.update_frag_new_pass_edit);
        rePassEdit = (EditText) view.findViewById(R.id.update_frag_re_pass_edit);
        updateButton = (Button) view.findViewById(R.id.update_frag_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = newPassEdit.getText().toString();
                String rePass = rePassEdit.getText().toString();
                if (pass.length() < 6 || !pass.equals(rePass)) {
                    Toast.makeText(getActivity(), "Passwords should match and should be of more than 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(pass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String errorMessage= task.getException().getMessage();
                                        Toast.makeText(getActivity(),errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        return view;
    }

}
