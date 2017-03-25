package com.example.snowiot.snowiotsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

TextView mBaseHeight;
    Button mRecalibrate;
    Switch mToggleDisplayActualDepth;

//    DatabaseReference mUserRootRef = FirebaseDatabase.getInstance().getReference();
//    DatabaseReference mUserRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + ((GlobalVariables) this.getApplication()).getUserUID());
//    DatabaseReference mUserSettings = mUserRootRef.child("users/" + userUIDpath);
//    DatabaseReference mRecalibrateFlag = mUserSettings.child("setNewBaseHeight");
//    DatabaseReference mCalibrationValue = mUserSettings.child("fixedHeight");
//    DatabaseReference mDeductBaseHeight = mUserSettings.child("deductBaseHeight");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBaseHeight = (TextView) findViewById(R.id.baseheight);
        mRecalibrate = (Button) findViewById(R.id.recalibrate);
        mToggleDisplayActualDepth = (Switch) findViewById(R.id.snowdepthswitch);


    }

    protected void onStart() {
        super.onStart();

        //Base Height Value Display
        DatabaseReference mUserRootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + ((GlobalVariables) this.getApplication()).getUserUID());     //Do not place this outside of onStart or onCreate functions as the global variable crashes the app
        DatabaseReference mCalibrationValue = mUserRootRef.child("fixedHeight");

        mCalibrationValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int baseHeight = dataSnapshot.getValue(int.class);
                mBaseHeight.setText(String.valueOf(baseHeight));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Switch for user to choose whether initial height measured from sensor will be deducted from height read, yielding height of snow rather than what sensor sees
        final DatabaseReference mDeductBaseHeight = mUserRootRef.child("deductBaseHeight");

        mToggleDisplayActualDepth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchPressed) {

                if (switchPressed) {
                    mDeductBaseHeight.setValue(1);
                } else {
                    mDeductBaseHeight.setValue(0);
                }

            }
        });

        //Set flag for microcontroller to get a new initial value and store on database
        final DatabaseReference mRecalibrateFlag = mUserRootRef.child("setNewBaseHeight");

        mRecalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecalibrateFlag.setValue(1);
            }
        });


    }
}
