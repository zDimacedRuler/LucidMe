package com.example.amankumar.lucidme.Model;

import com.example.amankumar.lucidme.Utils.Constants;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by AmanKumar on 5/27/2016.
 */
public class ChatModel {
    String userName;
    String lastMessage;
    HashMap<String,Object> lastUpdatedTimeStamp;

    public ChatModel() {
    }

    public ChatModel(String userName, String lastMessage) {
        this.userName = userName;
        this.lastMessage = lastMessage;
        HashMap<String,Object> obj=new HashMap<>();
        obj.put(Constants.CONSTANT_TIMESTAMP, ServerValue.TIMESTAMP);
        lastUpdatedTimeStamp=obj;
    }


    public String getUserName() {
        return userName;
    }

    public HashMap<String, Object> getLastUpdatedTimeStamp() {
        return lastUpdatedTimeStamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
