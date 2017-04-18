package com.example.snowiot.snowiotsimple;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserContactedWindow extends AppCompatActivity {



    /**
     * Area dedicated to notification experiment
     */

    NotificationManager notificationManager;

    boolean notificationActive = false;

    int notificationID = 1;


    /**
     * End of notification test
     */

    private Button mAcceptService, mDeclineService, mNotificationTest;
    private TextView mContacterName, mContactorInfo;
    private Driveways holdContacterInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacted_window);

        mAcceptService = (Button) findViewById(R.id.hireContacter);
        mDeclineService = (Button) findViewById(R.id.declineContacter);
        mNotificationTest = (Button) findViewById(R.id.notifytest);
        mContacterName = (TextView) findViewById(R.id.contacterName);
        mContactorInfo = (TextView) findViewById(R.id.contacterInfo);


        final DatabaseReference mUserHandleRef;
        final DatabaseReference mContactorHandleRef;
        DatabaseReference mRootRef;
        DatabaseReference mContactorInfoRef;

        mRootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference mUserInfoRef = mRootRef.child("driveways/" + ((GlobalVariables) this.getApplication()).getUserUID());
        mUserHandleRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + ((GlobalVariables) this.getApplication()).getUserUID() + "/requesthandle");
        mContactorHandleRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + ((GlobalVariables) this.getApplication()).getContacterUID() + "/requesthandle");
        mContactorInfoRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/driveways/" + ((GlobalVariables) this.getApplication()).getContacterUID());

        mContactorInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holdContacterInfo = dataSnapshot.getValue(Driveways.class);

                mContacterName.setText("Provider: " + holdContacterInfo.getName());
                mContacterName.setTextSize(18);
                mContacterName.setTextColor(Color.BLACK);
//                mContacterInfo.setText("Info: " + holdUserInfo.address.getStreet() + ", " + holdUserInfo.address.getCity() + ", " + holdUserInfo.address.getState());
//                mContacterInfo.setTextSize(18);
//                mContacterInfo.setTextColor(Color.BLACK);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAcceptService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContactorHandleRef.child("acceptdecline").setValue(1); //Use contactor's database section instead since there is a possibility that the user might accept someone else's offer
//                mContactorHandleRef.child("recipientUserID").setValue(((GlobalVariables) getApplication()).getUserUID());
                mUserInfoRef.child("status").setValue(3);                             //Turn user marker on map blue
                mUserHandleRef.child("prompt").setValue(0);             //If user accepts offer then reset prompt that triggers notification
                mUserHandleRef.child("jobAssignedToUID").setValue(((GlobalVariables) getApplication()).getContacterUID());
                mContactorHandleRef.child("jobDeliveredToUID").setValue(((GlobalVariables) getApplication()).getUserUID());
                finish();
            }
        });


        mDeclineService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContactorHandleRef.child("acceptdecline").setValue(0); //Use contactor's database section instead since there is a possibility that the user might accept someone else's offer
//                mContactorHandleRef.child("recipientUserID").setValue(((GlobalVariables) getApplication()).getUserUID());
                mUserHandleRef.child("prompt").setValue(0);             //If user declines offer then reset prompt that triggers notification
                mUserHandleRef.child("jobAssignedToUID").setValue("null");
                mContactorHandleRef.child("jobDeliveredToUID").setValue("null");
                finish();
            }
        });


        mNotificationTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification();
            }
        });


    }


    /**
     * Area dedicated to notification experiment
     */

public void showNotification (){

    //Set notification message contents
    NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
            NotificationCompat.Builder(this)
            .setContentTitle("Test Title")
            .setContentText("Test Message")
            .setTicker("Alert New Message")
            .setAutoCancel(true)                                    //Automatically clear notification when clicked on the task bar
            .setSmallIcon(R.drawable.com_facebook_button_icon);

    //Set intent pointing to activity that will open when the notification is clicked
    Intent mainActivity = new Intent(this, MainActivity.class);
    Intent userContacted = new Intent(this, UserContactedWindow.class);

    //Stackbuilder to make it so that when user hits "back", the app goes to the right place
    //and doesnt open up another app

    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);

    //Add current parents of activity to this stack

    taskStackBuilder.addParentStack(UserContactedWindow.class);

    //Add intent defined to the stack

    taskStackBuilder.addNextIntent(userContacted);

    //Define action & intent to perform with this intent by another app

    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
           PendingIntent.FLAG_UPDATE_CURRENT);          //Check if this intent is already open, if so, then update it instead of opening brand new activity


    //Define intent to be launched when the notification is clicked on in the task bar

    notificationBuilder.setContentIntent(pendingIntent);

    //Get manager to notify of events that happen in the background

    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    //Post notification

    notificationManager.notify(notificationID, notificationBuilder.build());

    //Check whether it is currently on the notifcation window

    notificationActive = true;
}


    /**
     * End of notification test
     */


}
