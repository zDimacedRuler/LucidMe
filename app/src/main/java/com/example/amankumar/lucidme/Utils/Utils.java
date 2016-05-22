package com.example.amankumar.lucidme.Utils;

import android.content.Context;

import java.text.SimpleDateFormat;

/**
 * Created by AmanKumar on 3/31/2016.
 */
public class Utils {
    private Context mContext=null;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd");
    public static String getMonth(int monthOfYear) {
        switch (monthOfYear){
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "Aug";
            case 8:
                return "Sept";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "";
        }
    }
    public static String getDay(int dayOfMonth) {
        switch (dayOfMonth){
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "";
        }
    }
}
