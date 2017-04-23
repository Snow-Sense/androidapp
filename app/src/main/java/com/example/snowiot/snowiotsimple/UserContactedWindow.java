package com.example.snowiot.snowiotsimple;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    private ImageView mContactorPhoto;
    private Driveways holdContacterInfo;

    private StorageReference mDrivewayPhotoFolder = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_contacted_window);

        mAcceptService = (Button) findViewById(R.id.hireContacter);
        mDeclineService = (Button) findViewById(R.id.declineContacter);
//        mNotificationTest = (Button) findViewById(R.id.notifytest);
        mContacterName = (TextView) findViewById(R.id.contacterName);
        mContactorInfo = (TextView) findViewById(R.id.contacterInfo);
        mContactorPhoto = (ImageView) findViewById(R.id.snowPlowPhoto);

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


        downloadSnowPlowPhoto();

    }


    public void downloadSnowPlowPhoto() {

        String userUID = ((GlobalVariables) this.getApplication()).getContacterUID();

        mDrivewayPhotoFolder.child("users/" + userUID + "/profiledriveway.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(getApplicationContext()).load(uri).into(mContactorPhoto);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });


    }

}
