package com.example.snowiot.snowiotsimple;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    //to check if user is already or still logged in
    private FirebaseAuth.AuthStateListener mAuthListener;
    //define firebase auth
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    Switch mSetLedSignal;
    ListView mListView;
//    ImageView snowIotTitle;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLedSignalRef = mRootRef.child("ledSignal");    //reference to LED status variable


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.sensorListView);
            mSetLedSignal = (Switch) findViewById(R.id.ledSignal);
//            snowIotTitle = (ImageView) findViewById(R.id.snowIotText);

//            Picasso.with(getApplicationContext()).load("http://i.imgur.com/qoku2bl.png").into(snowIotTitle); //pass image into imgview

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser() == null) {             //If getCurrentUser == null no user is logged in.

                        startActivity(new Intent(MainActivity.this, Login.class)); //if user is not logged in, go back to login activity and end main activity.
                        finish();                                                  //end activity
                    }

                }
            };

            mAuth = FirebaseAuth.getInstance();         //get current instance of whos authenticated
            mUser = FirebaseAuth.getInstance().getCurrentUser();        //get current user, note FirebaseUser is not the same as FirebaseAuth

            ((GlobalVariables) this.getApplication()).storeUserUID(mUser.getUid());      //store useruid in a global variable so that it can be accessed by all activities

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/sensors/" + mUser.getUid() + "/livesensor");      //dynamic reference based on user logged in

            FirebaseListAdapter mAdapter = new FirebaseListAdapter<Sensors>(this, Sensors.class, android.R.layout.simple_list_item_2, databaseReference) {          //Listview using Sensors class
                @Override
                protected void populateView(View view, Sensors chatMessage, int position) {
                    ((TextView)view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
                    ((TextView)view.findViewById(android.R.id.text1)).setTextSize(24);
                    ((TextView)view.findViewById(android.R.id.text2)).setText(chatMessage.getState());
                    ((TextView)view.findViewById(android.R.id.text2)).setTextSize(24);

                }
            };
            mListView.setAdapter(mAdapter);


        }


    @Override   //Added this too
    protected void onStart() {
        super.onStart();

        //LED Switch code block
        mSetLedSignal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchPressed) {

                if (switchPressed) {
                    Toast.makeText(getApplicationContext(), "Sensors ON", Toast.LENGTH_SHORT).show(); //Display message on screen.
                    mLedSignalRef.setValue(1);  //if switch is checked, then set LED signal to 1 (on firebase)
                } else {
                    Toast.makeText(getApplicationContext(), "Sensors OFF", Toast.LENGTH_SHORT).show();
                    mLedSignalRef.setValue(0);  //if switch is not checked, then set LED signal to 0 (on firebase)
                }

            }
        });

        mAuth.addAuthStateListener(mAuthListener);  //Add authenticator state listener


    }

//Code by Andres Menendez (Youtube)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_blog) {
            Intent blogLink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snow-sense.github.io/IoTSnow/"));
            startActivity(blogLink);
            return true;
        }

        if (id == R.id.action_credits) {
            Intent creditsSelect = new Intent(MainActivity.this, Credits.class);
            startActivity(creditsSelect);
            return true;
        }

        if (id == R.id.action_map) {
            Intent drivewaySensorsMap = new Intent(MainActivity.this, maps.class);
            startActivity(drivewaySensorsMap);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent userSettings = new Intent(MainActivity.this, Settings.class);
            startActivity(userSettings);
            return true;
        }

        if (id == R.id.action_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent returnToLoginScreen = new Intent(MainActivity.this, Login.class);
            startActivity(returnToLoginScreen);
            finish();   //end main activity after logging out
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//Back button won't close MainActivity
  /*  @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
    */
}
