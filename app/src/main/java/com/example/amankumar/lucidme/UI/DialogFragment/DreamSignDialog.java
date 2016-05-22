package com.example.amankumar.lucidme.UI.DialogFragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amankumar.lucidme.Model.DreamSignModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;

public class DreamSignDialog extends DialogFragment {
    AutoCompleteTextView dreamSignEdit;
    ListView dreamList;
    Firebase ref, userRef;
    FirebaseListAdapter<DreamSignModel> firebaseListAdapter;
    ArrayList<String> selectedItems;
    SharedPreferences sp;
    String encodedEmail;
    ArrayList<String> dreamSignList;
    ArrayAdapter<String> stringArrayAdapter;
    Query query;

    public DreamSignDialog() {
        // Required empty public constructor
    }

    public static DreamSignDialog newInstance(ArrayList<String> dreamSigns) {
        DreamSignDialog fragment = new DreamSignDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("dreamSign", dreamSigns);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItems = new ArrayList<>();
        assert savedInstanceState != null;
        selectedItems = getArguments().getStringArrayList("dreamSign");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        encodedEmail = sp.getString(Constants.CURRENT_USER, "");
        dreamSignList = new ArrayList<>();
        userRef = new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_dream_signs, null);
        dreamSignEdit = (AutoCompleteTextView) rootView.findViewById(R.id.dialog_add_dream);
        dreamList = (ListView) rootView.findViewById(R.id.dialog_listView);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constants.LOCATION_DREAMS_SIGNS).exists()) {
                    ref = new Firebase(Constants.FIREBASE_USERS_URL).child(encodedEmail).child(Constants.LOCATION_DREAMS_SIGNS);
                    query = ref.orderByValue();
                    dreamSignList.clear();
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                dreamSignList.add(child.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    /*stringArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, dreamSignList);
                    dreamSignEdit.setAdapter(stringArrayAdapter);*/
                    firebaseListAdapter = new FirebaseListAdapter<DreamSignModel>(getActivity(), DreamSignModel.class, R.layout.dialog_list_view, query) {
                        @Override
                        protected void populateView(View v, DreamSignModel model, int position) {
                            super.populateView(v, model, position);
                            TextView dreamSignText = (TextView) v.findViewById(R.id.dialog_list_text);
                            dreamSignText.setText(model.getDreamSign());
                            if (selectedItems.contains(model.getDreamSign())) {
                                dreamSignText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_box_gray, 0);
                            } else {
                                dreamSignText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_outline_gray, 0);

                            }
                        }
                    };
                    dreamList.setAdapter(firebaseListAdapter);
                    dreamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView dreamSignText = (TextView) view.findViewById(R.id.dialog_list_text);
                            DreamSignModel dreamSignModel = firebaseListAdapter.getItem(position);
                            String sign = dreamSignModel.getDreamSign();
                            if (selectedItems.contains(sign)) {
                                dreamSignText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_outline_gray, 0);
                                selectedItems.remove(sign);
                            } else {
                                dreamSignText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_box_gray, 0);
                                selectedItems.add(sign);
                            }
                        }
                    });
                    dreamList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            final String listId = firebaseListAdapter.getRef(position).getKey();
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
                            deleteBuilder.setMessage("Delete DreamSign?");
                            deleteBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ref.child(listId).setValue(null);
                                }
                            });
                            deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = deleteBuilder.create();
                            dialog.show();
                            return true;
                        }
                    });

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        dreamSignEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String text = dreamSignEdit.getText().toString();
                    if (text.equals(""))
                        return false;
                    userRef.child(Constants.LOCATION_DREAMS_SIGNS).child(text).setValue(text);
                    dreamSignEdit.setText("");
                    Toast.makeText(getActivity(), "DreamSign Added", Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });
        builder.setView(rootView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditNameDialogListener listener = (EditNameDialogListener) getActivity();
                listener.onFinishedDialog(selectedItems);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    public interface EditNameDialogListener {
        void onFinishedDialog(ArrayList<String> dreamSigns);
    }
}
