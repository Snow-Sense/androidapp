package com.example.snowiot.snowiotsimple;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class DrivewayPhoto extends AppCompatActivity {

//Credits to "TVAC Studio" on youtube for step-by-step tutorial on how to get firebase storage, and picture upload working.
// This is an experimental activity.

    Button mUpload;
    ImageView mDrivewayPicture;

    static final int REQUEST_TAKE_PHOTO = 1;

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mDrivewayPhotoFolder = FirebaseStorage.getInstance().getReference();

    private ProgressDialog mProgressDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveway_photo);

        mUpload = (Button) findViewById(R.id.uploadpicture);
        mDrivewayPicture = (ImageView) findViewById(R.id.drivewaypicture);

        final String userUID = ((GlobalVariables) this.getApplication()).getUserUID();

        mProgressDialogue = new ProgressDialog(this);

//        Uri photoReceive = mDrivewayPhotoFolder.child("users/" + userUID + "/Chrysanthemum.jpg").getDownloadUrl().getResult();

        mDrivewayPhotoFolder.child("butters.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Toast.makeText(getApplicationContext(), "Download successful.", Toast.LENGTH_SHORT).show();
                Picasso.with(getApplicationContext()).load(uri).into(mDrivewayPicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "Download failed.", Toast.LENGTH_SHORT).show();
            }
        });

//        Picasso.with(getApplicationContext()).load(photoReceive).into(mDrivewayPicture);

        mUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, REQUEST_TAKE_PHOTO);


            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_TAKE_PHOTO) && (resultCode == RESULT_OK)) {

            Uri PhotoUri = data.getData();

//            .putExtra(MediaStore.EXTRA_OUTPUT, uri);

            if (PhotoUri == null) {
                Toast.makeText(DrivewayPhoto.this, "Photo was not captured, URI is null.", Toast.LENGTH_LONG).show();
            } else {

                mProgressDialogue.setMessage("Upload on going");
                mProgressDialogue.show();

                StorageReference userFolder = mDrivewayPhotoFolder.child("photos").child(PhotoUri.getLastPathSegment());

                userFolder.putFile(PhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        mProgressDialogue.dismiss();

                        Toast.makeText(DrivewayPhoto.this, "Upload finished", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Toast.makeText(DrivewayPhoto.this, "Error", Toast.LENGTH_LONG).show();
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }
}
