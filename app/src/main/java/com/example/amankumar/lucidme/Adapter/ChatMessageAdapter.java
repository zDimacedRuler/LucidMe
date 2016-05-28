package com.example.amankumar.lucidme.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.ChatMessageModel;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by AmanKumar on 5/28/2016.
 */
public class ChatMessageAdapter extends FirebaseListAdapter<ChatMessageModel> {
    Activity context;
    SharedPreferences sp;
    String encodedEmail;
    public ChatMessageAdapter(Activity activity, Class<ChatMessageModel> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        context=activity;
    }
    @Override
    protected void populateView(View v, ChatMessageModel model, int position) {
        sp= PreferenceManager.getDefaultSharedPreferences(context);
        encodedEmail=sp.getString(Constants.CURRENT_USER,"");
        TextView leftMessageText = (TextView) v.findViewById(R.id.leftmessageText_LV);
        TextView rightMessageText = (TextView) v.findViewById(R.id.rightmessageText_LV);
        String author=model.getAuthor();
        if(author.equals(encodedEmail)){
            rightMessageText.setVisibility(View.VISIBLE);
            rightMessageText.setText(model.getMessage());
            leftMessageText.setVisibility(View.GONE);
        }
        else{
            leftMessageText.setVisibility(View.VISIBLE);
            leftMessageText.setText(model.getMessage());
            rightMessageText.setVisibility(View.GONE);
        }
    }
}
