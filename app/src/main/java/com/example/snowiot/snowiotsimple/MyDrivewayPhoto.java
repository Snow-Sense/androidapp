package com.example.snowiot.snowiotsimple;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class MyDrivewayPhoto extends AppCompatActivity {


    private TextView mPhotoUploadedOn, mPhotoUploadedBy;
    private ImageView mPlowedDrivewayPhoto;

    private StorageReference mDrivewayPhotoFolder = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_driveway_photo);

        mPlowedDrivewayPhoto = (ImageView) findViewById(R.id.drivewayFinishedPhoto);

        downloadPlowedDrivewayPhoto();

    }

    public void downloadPlowedDrivewayPhoto(){

        String appUserUID = ((GlobalVariables) this.getApplication()).getUserUID();

        mDrivewayPhotoFolder.child("users/" + appUserUID + "/drivewayfinished.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(getApplicationContext()).load(uri).into(mPlowedDrivewayPhoto);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Download failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
