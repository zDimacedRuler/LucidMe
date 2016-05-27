package com.example.amankumar.lucidme.UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amankumar.lucidme.R;

/**
 * Created by AmanKumar on 4/30/2016.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView titleText,dreamText,dayText,dateText,labelText;
    ImageView lucidImage,labelImage;
    View view;
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        titleText= (TextView) itemView.findViewById(R.id.text_view_titleLV);
        dreamText= (TextView) itemView.findViewById(R.id.text_view_dreamLV);
        dayText= (TextView) itemView.findViewById(R.id.text_view_dayLV);
        dateText= (TextView) itemView.findViewById(R.id.text_view_dateLV);
        lucidImage= (ImageView) itemView.findViewById(R.id.image_view_lucidLV);
        labelText= (TextView) itemView.findViewById(R.id.text_view_labelLV);
        labelImage= (ImageView) itemView.findViewById(R.id.image_view_labelLV);
    }
}