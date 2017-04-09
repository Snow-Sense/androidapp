package com.example.snowiot.snowiotsimple;

import android.app.Application;
import android.net.Uri;

/**
 * Created by Felipe on 3/24/2017.
 */

public class GlobalVariables extends Application{

    private String userUID;
    private Uri savedUri;
    private String userUIDFromMap;

    public void storeUserUID(String UID){
        userUID = UID;
    }

    public String getUserUID(){
        return userUID;
    }

    public void setSavedUri(Uri uri){
        savedUri = uri;
    }

    public Uri getSavedUri(){
        return savedUri;
    }


    public void setUserUIDFromMap(String UID){
        userUIDFromMap = UID;
    }

    public String getUserUIDFromMap(){
        return userUIDFromMap;
    }
}
