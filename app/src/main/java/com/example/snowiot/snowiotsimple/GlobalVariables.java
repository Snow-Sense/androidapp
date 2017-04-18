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
    private String contacterUID;
    private String jobDeliveredToUID;                       //Snowplow owner sees this - ID of the user whose snowplow owner has been assigned to
    private String jobAssignedToUID;                        //Sensor owner sees this - ID of snowplow owner currently in charge of clearing driveway
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

    public void setContacterUID(String UID){
        contacterUID = UID;
    }

    public String getContacterUID(){
        return contacterUID;
    }

    public void setJobDeliveredToUID(String UID){
        jobDeliveredToUID = UID;
    }

    public String getJobDeliveredToUID(){
        return jobDeliveredToUID;
    }

    public void setJobAssignedToUID(String UID){
        jobAssignedToUID = UID;
    }

    public String getJobAssignedToUID(){
        return jobAssignedToUID;
    }
}
