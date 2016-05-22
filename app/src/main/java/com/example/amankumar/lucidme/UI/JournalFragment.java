package com.example.amankumar.lucidme.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerViewAdapter;

import java.util.Calendar;

public class JournalFragment extends Fragment {
    String currentUser;
    SharedPreferences sp;
    Firebase dreamRef, userDreamRef;
    RecyclerView dreamRecyclerView;
    Query query;
    RecyclerViewAdapter recyclerViewAdapter;
    CardView noDreamCardView;
    Typeface typeface;
    public JournalFragment() {
        // Required empty public constructor
    }
    public static JournalFragment newInstance() {
        JournalFragment fragment = new JournalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        dreamRecyclerView = (RecyclerView) view.findViewById(R.id.dreamRecyclerView);
        noDreamCardView = (CardView) view.findViewById(R.id.DF_no_dream_card_view);
        dreamRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser);
        dreamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constants.LOCATION_DREAMS).exists()) {
                    noDreamCardView.setVisibility(View.GONE);
                    userDreamRef = new Firebase(Constants.FIREBASE_USERS_URL).child(currentUser).child(Constants.LOCATION_DREAMS);
                    query = userDreamRef.orderByChild("calendar");
                    recyclerViewAdapter = new RecyclerViewAdapter(Dream.class, R.layout.listview, RecyclerViewHolder.class, query);
                    dreamRecyclerView.setLayoutManager(new NpaGridLayoutManager(getActivity()));
//                    dreamRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
                    dreamRecyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    noDreamCardView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);
    }

    public class RecyclerViewAdapter extends FirebaseRecyclerViewAdapter<Dream, RecyclerViewHolder> {

        public RecyclerViewAdapter(Class<Dream> modelClass, int modelLayout, Class<RecyclerViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        protected void populateViewHolder(RecyclerViewHolder viewHolder, Dream model, int position) {
            super.populateViewHolder(viewHolder, model, position);
            viewHolder.titleText.setText(model.getTitleDream());
            viewHolder.dreamText.setText(model.getDream());
            Calendar calendar = model.getCalendar();
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH);
            String dateText=dayOfMonth+"/"+month+"/"+year;
            viewHolder.dayText.setText(Utils.getDay(dayOfWeek));
            viewHolder.dateText.setText(dateText);
            String lucid=model.getLucid();
            if(lucid.equals("True"))
                viewHolder.lucidImage.setVisibility(View.VISIBLE);
            else
                viewHolder.lucidImage.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder viewHolder, final int position) {
            super.onBindViewHolder(viewHolder, position);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String listId = recyclerViewAdapter.getRef(position).getKey();
                    Intent intent = new Intent(getActivity(), DreamDetailActivity.class);
                    intent.putExtra("listId", listId);
                    startActivity(intent);

                }
            });
        }
    }
    private static class NpaGridLayoutManager extends LinearLayoutManager {
        public NpaGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public NpaGridLayoutManager(Context context) {
            super(context);
        }

        public NpaGridLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

    }
}
