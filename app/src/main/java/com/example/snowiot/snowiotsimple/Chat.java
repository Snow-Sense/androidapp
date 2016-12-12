package com.example.snowiot.snowiotsimple;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by Felipe on 12/10/2016
 */

public class Chat {
    private String name;
    private String text;
    private String uid;

    public Chat() {
    }

    public Chat(String name, String uid, String message) {
        this.name = name;
        this.text = message;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }
}