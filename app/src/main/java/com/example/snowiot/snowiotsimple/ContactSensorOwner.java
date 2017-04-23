package com.example.snowiot.snowiotsimple;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

public class ContactSensorOwner extends AppCompatActivity {

    private Button mSendRequest;
    private TextView mUserName;
    private TextView mUserAddress;
    private TextView mSnowAccumulation;
    private ImageView mDrivewayPhoto;
    private Driveways holdUserInfo;

    private StorageReference mDrivewayPhotoFolder = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sensor_owner);


        mSendRequest = (Button) findViewById(R.id.sendJobRequest);
        mUserName = (TextView) findViewById(R.id.contactName);
        mUserAddress = (TextView) findViewById(R.id.contactAddress);
        mSnowAccumulation = (TextView) findViewById(R.id.contactSnowAccumulation);
        mDrivewayPhoto = (ImageView) findViewById(R.id.contactDrivewayPhoto);


        final DatabaseReference mHandleRef;
        DatabaseReference mUserInfo;
        DatabaseReference mUserSensorInfo;

        mHandleRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + ((GlobalVariables) this.getApplication()).getUserUIDFromMap() + "/requesthandle");
        mUserInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/driveways/" + ((GlobalVariables) this.getApplication()).getUserUIDFromMap());
        mUserSensorInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/sensors/" + ((GlobalVariables) this.getApplication()).getUserUIDFromMap() + "/livesensor/Height/state");

        downloadDrivewayPhoto();

mUserInfo.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        holdUserInfo = dataSnapshot.getValue(Driveways.class);

        mUserName.setText("Owner: " + holdUserInfo.getName());
        mUserName.setTextSize(18);
        mUserName.setTextColor(Color.BLACK);
        mUserAddress.setText("Address: " + holdUserInfo.address.getStreet() + ", " + holdUserInfo.address.getCity() + ", " + holdUserInfo.address.getState());
        mUserAddress.setTextSize(18);
        mUserAddress.setTextColor(Color.BLACK);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

        mUserSensorInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String snowAccumulation = dataSnapshot.getValue(String.class);

                if (snowAccumulation != null) {
                    mSnowAccumulation.setText("Snow Accumulated: " + snowAccumulation);
                    mSnowAccumulation.setTextSize(18);
                    mSnowAccumulation.setTextColor(Color.BLACK);
                }

                else{
                    mSnowAccumulation.setText("Snow Accumulated: No sensor detected");
                    mSnowAccumulation.setTextSize(18);
                    mSnowAccumulation.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holdUserInfo.getStatus() == 2) {            //Red marker
                    mHandleRef.child("prompt").setValue(1);
                    mHandleRef.child("senderID").setValue(((GlobalVariables) getApplication()).getUserUID());                    //writes the UID of the sender on the recipient's database section so that the sender's information are loaded on the recipient's screen
                    Toast.makeText(getApplicationContext(), "Offer sent to " + holdUserInfo.getName(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if (holdUserInfo.getStatus() == 1){        //Green marker
                    Toast.makeText(getApplicationContext(), "User currently does not need service.", Toast.LENGTH_SHORT).show();
                }
                else if (holdUserInfo.getStatus() == 3){        //Blue marker
                    Toast.makeText(getApplicationContext(), "User's driveway is already being plowed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
}

    public void downloadDrivewayPhoto(){

            String userUID = ((GlobalVariables) this.getApplication()).getUserUIDFromMap();

        mDrivewayPhotoFolder.child("users/" + userUID + "/profiledriveway.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(getApplicationContext()).load(uri).into(mDrivewayPhoto);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }



}