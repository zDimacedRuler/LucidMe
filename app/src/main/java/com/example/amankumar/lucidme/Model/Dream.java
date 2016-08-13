package com.example.amankumar.lucidme.Model;

import com.example.amankumar.lucidme.Utils.Constants;

import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by AmanKumar on 4/1/2016.
 */
public class Dream {
    String titleDream;
    String dream;
    long dateOfDream;
    long revDateOfDream;
    HashMap<String,Object> timeStampLastChanged;
    HashMap<String,Object> userDreamSigns;
    String lucid;
    String lucidTechnique;
    String additionalNotes;

    public Dream() {
    }

    public Dream(String titleDream, String dream, long dateOfDream, HashMap<String,Object> userDreamSigns, String lucid, String lucidTechnique, String additionalNotes) {
        this.titleDream = titleDream;
        this.dream = dream;
        this.dateOfDream = dateOfDream;
        this.revDateOfDream=0-dateOfDream;
        this.userDreamSigns=userDreamSigns;
        this.lucid=lucid;
        this.lucidTechnique=lucidTechnique;
        this.additionalNotes=additionalNotes;
        GregorianCalendar calendar=(GregorianCalendar) GregorianCalendar.getInstance();
        long milli=calendar.getTimeInMillis();
        HashMap<String,Object> obj=new HashMap<>();
        obj.put(Constants.CONSTANT_TIMESTAMP,0-milli);
        this.timeStampLastChanged=obj;
    }

    public String getDream() {
        return dream;
    }
    public String getTitleDream() {
        return titleDream;
    }
    public HashMap<String, Object> getTimeStampLastChanged() {
        return timeStampLastChanged;
    }

    public HashMap<String, Object> getUserDreamSigns() {
        return userDreamSigns;
    }

    public String getLucid() {
        return lucid;
    }

    public String getLucidTechnique() {
        return lucidTechnique;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public long getDateOfDream() {
        return dateOfDream;
    }
}
