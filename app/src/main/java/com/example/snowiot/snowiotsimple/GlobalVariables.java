package com.example.snowiot.snowiotsimple;

import android.app.Application;

/**
 * Created by Felipe on 3/24/2017.
 */

public class GlobalVariables extends Application{

    private String userUID;

    public void storeUserUID(String UID){
        userUID = UID;
    }

    public String getUserUID(){
        return userUID;
    }
}
