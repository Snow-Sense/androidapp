package com.example.snowiot.snowiotsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    Switch mSetLedSignal;
    ListView mListView;


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLedSignalRef = mRootRef.child("ledSignal");    //reference to LED status variable


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.sensorListView);
            mSetLedSignal = (Switch) findViewById(R.id.ledSignal);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/Sensor1");
            //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            FirebaseListAdapter mAdapter = new FirebaseListAdapter<Sensors>(this, Sensors.class, android.R.layout.two_line_list_item, databaseReference) {
                @Override
                protected void populateView(View view, Sensors chatMessage, int position) {
                    ((TextView)view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
                    ((TextView)view.findViewById(android.R.id.text2)).setText(chatMessage.getState());

                }
            };
            mListView.setAdapter(mAdapter);


        }


    @Override   //Added this too
    protected void onStart() {
        super.onStart();

        //LED Switch block
        mSetLedSignal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchPressed) {

                if (switchPressed) {
                    Toast.makeText(getApplicationContext(), "Sensors ON.", Toast.LENGTH_SHORT).show(); //Display message on screen.
                    mLedSignalRef.setValue(1);  //if switch is checked, then set LED signal to 1 (on firebase)
                } else {
                    Toast.makeText(getApplicationContext(), "Sensors OFF", Toast.LENGTH_SHORT).show();
                    mLedSignalRef.setValue(0);  //if switch is not checked, then set LED signal to 0 (on firebase)
                }

            }
        });

    }



        }
