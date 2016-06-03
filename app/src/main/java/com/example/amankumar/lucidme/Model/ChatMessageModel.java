package com.example.amankumar.lucidme.Model;

import com.example.amankumar.lucidme.Utils.Constants;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by AmanKumar on 5/27/2016.
 */
public class ChatMessageModel {
    String author;
    String message;
    HashMap<String,Object> messageTimestamp;
    String sharedDream;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String author, String message) {
        this.author = author;
        this.message = message;
        HashMap<String,Object> obj=new HashMap<>();
        obj.put(Constants.CONSTANT_TIMESTAMP, ServerValue.TIMESTAMP);
        messageTimestamp=obj;
        sharedDream="false";
    }

    public ChatMessageModel(String message, String sharedDream, String author) {
        this.message = message;
        HashMap<String,Object> obj=new HashMap<>();
        obj.put(Constants.CONSTANT_TIMESTAMP, ServerValue.TIMESTAMP);
        messageTimestamp=obj;
        this.sharedDream = sharedDream;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, Object> getMessageTimestamp() {
        return messageTimestamp;
    }

    public String getSharedDream() {
        return sharedDream;
    }

}
