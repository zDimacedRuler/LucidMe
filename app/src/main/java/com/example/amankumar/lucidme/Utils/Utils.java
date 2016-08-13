package com.example.amankumar.lucidme.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.example.amankumar.lucidme.UI.LockActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by AmanKumar on 3/31/2016.
 */
public class Utils {
    private Context mContext = null;
    public static Date appPauseTime;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getMonth(int monthOfYear) {
        switch (monthOfYear) {
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
        switch (dayOfMonth) {
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

    public static int compareEmail(String email1, String email2) {
        return email1.compareTo(email2);
    }

    public static void lockAppStoreTime(Activity act) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
        Boolean block = sp.getBoolean(Constants.BLOCK_STATE, true);
        if (!block)
            appPauseTime = new Date();
    }
    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    public static void lockAppCheck(Activity act) {
        boolean bLock = false;

        // Check to see if this is the first time through app
        if (appPauseTime == null) {
            bLock = true;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
            SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean(Constants.BLOCK_STATE, true).apply();
        } else {
            Date currTime = new Date();
            long diffMillis = currTime.getTime() - appPauseTime.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffMillis);

            // Lock app if 120 seconds (2 minutes) has lapsed
            if (diffInSec > 120) {
                bLock = true;
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(act);
                SharedPreferences.Editor spe = sp.edit();
                spe.putBoolean(Constants.BLOCK_STATE, true).apply();
            }
        }

        if (bLock) {
            Intent intent = new Intent(act, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            act.startActivity(intent);
        }
    }
}
