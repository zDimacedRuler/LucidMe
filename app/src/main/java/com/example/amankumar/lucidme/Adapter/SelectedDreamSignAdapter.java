package com.example.amankumar.lucidme.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Model.Dream;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.UI.SelectedDreamSignActivity;
import com.example.amankumar.lucidme.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectedDreamSignAdapter extends ArrayAdapter<Dream>{
    Context context;
    int layoutResourceId;
    ArrayList<Dream> dreams;
    int positionOfDream;
    ArrayList<Object> list;
    public SelectedDreamSignAdapter(Context context, int resource, List<Dream> dreams,ArrayList<Object> list) {
        super(context, resource, dreams);
        this.layoutResourceId = resource;
        this.context = context;
        this.dreams = (ArrayList<Dream>) dreams;
        this.list= list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DreamHolder holder;
        positionOfDream=position;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DreamHolder();
            holder.titleText= (TextView) row.findViewById(R.id.text_view_titleLV);
            holder.dreamText= (TextView) row.findViewById(R.id.text_view_dreamLV);
            holder.dayText= (TextView) row.findViewById(R.id.text_view_dayLV);
            holder.dateText= (TextView) row.findViewById(R.id.text_view_dateLV);
            holder.lucidImage= (ImageView) row.findViewById(R.id.image_view_lucidLV);

            row.setTag(holder);
        }
        else
        {
            holder = (DreamHolder)row.getTag();
        }

        Dream model = dreams.get(position);
        holder.titleText.setText(model.getTitleDream());
        holder.dreamText.setText(model.getDream());
        Calendar calendar = model.getCalendar();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        String dateText=dayOfMonth+"/"+month+"/"+year;
        holder.dayText.setText(Utils.getDay(dayOfWeek));
        holder.dateText.setText(dateText);
        String lucid=model.getLucid();
        if(lucid.equals("True"))
            holder.lucidImage.setVisibility(View.VISIBLE);
        else
            holder.lucidImage.setVisibility(View.GONE);
        row.setOnClickListener(new OnItemClickListener(position,list.get(position).toString()));
        return row;
    }

    static class DreamHolder
    {
        TextView titleText,dreamText,dayText,dateText;
        ImageView lucidImage;
    }
    private class OnItemClickListener implements View.OnClickListener{
        int mPosition;
        String mListId;
        public OnItemClickListener(int position,String listId) {
            mPosition=position;
            mListId=listId;
        }

        @Override
        public void onClick(View v) {
            SelectedDreamSignActivity selectedDreamSignActivity= (SelectedDreamSignActivity) context;
            selectedDreamSignActivity.onItemClick(mPosition,mListId);
        }
    }
}
