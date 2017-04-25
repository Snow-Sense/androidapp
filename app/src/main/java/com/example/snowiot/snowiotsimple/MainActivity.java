package com.example.snowiot.snowiotsimple;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {



    /**
     * Area dedicated to notification experiment
     */

    NotificationManager notificationManager;

    boolean notificationActive = false;

    int notificationID = 1;

    /**
     * End of notification test
     */


    //to check if user is already or still logged in
    private FirebaseAuth.AuthStateListener mAuthListener;
    //define firebase auth
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Switch mSetLedSignal;
    private ListView mListView;
    private TextView mLastUpdatedOn;
//    ImageView snowIotTitle;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLedSignalRef = mRootRef.child("ledSignal");    //reference to LED status variable

    int metricMode = 1;
    String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLastUpdatedOn = (TextView) findViewById(R.id.timeStamp);
        mListView = (ListView) findViewById(R.id.sensorListView);
        mSetLedSignal = (Switch) findViewById(R.id.ledSignal);

//            snowIotTitle = (ImageView) findViewById(R.id.snowIotText);

//            Picasso.with(getApplicationContext()).load("http://i.imgur.com/qoku2bl.png").into(snowIotTitle); //pass image into imgview

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {             //If getCurrentUser == null no user is logged in.

                    startActivity(new Intent(MainActivity.this, Login.class)); //if user is not logged in, go back to login activity and end main activity.
                    finish();                                                  //end activity
                    finish();                                                  //end activity
                }

            }
        };

        mAuth = FirebaseAuth.getInstance();         //get current instance of whos authenticated
        mUser = FirebaseAuth.getInstance().getCurrentUser();        //get current user, note FirebaseUser is not the same as FirebaseAuth

        ((GlobalVariables) this.getApplication()).storeUserUID(mUser.getUid());      //store useruid in a global variable so that it can be accessed by all activities

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/sensors/" + mUser.getUid() + "/livesensor");      //dynamic reference based on user logged in
        DatabaseReference mUserHandleRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://snowtotals-68015.firebaseio.com/users/" + mUser.getUid() + "/requesthandle"); //dynamic reference to requesthandle
        //Determine user type at the main activity so that when other activities are called, a different activity will be called depending on whether user is snowplow owner or sensor owner.
        DatabaseReference mUserTypeRef = mRootRef.child("driveways/" + mUser.getUid() + "/type");
        mUserTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userType = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Monitor whether a snow plow has provided the user their ID, which is used in the confirmation page
        DatabaseReference mContacterUID = mRootRef.child("users/" + mUser.getUid() + "/requesthandle/senderID");
        mContacterUID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((GlobalVariables) getApplication()).setContacterUID(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Monitor when "prompt" flag changes in the database to trigger notification
        final DatabaseReference mPromptNotification = mUserHandleRef.child("prompt");
        mPromptNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int p = dataSnapshot.getValue(Integer.class);

                if (p == 1){
                    alertSensorUserOfService();
                }
                else if (p == 2){
                    generalNotification("Snow-Sense Service Provider", "Snowplow has cancelled the job. Your location was placed back on the map.", "Alert: Snowplow has quit the job.");
                    mPromptNotification.setValue(0);
                    DatabaseReference mUserDriveway = mRootRef.child("driveways/" + mUser.getUid());
                    mUserDriveway.child("status").setValue(2);
                }
                else if (p == 3){
                    generalNotification("Snow-Sense Service Provider", "Your driveway has been plowed. Click to see the picture. ", "Alert: Your driveway has been plowed.");
                    mPromptNotification.setValue(0);
                    DatabaseReference mUserDriveway = mRootRef.child("driveways/" + mUser.getUid());
                    mUserDriveway.child("status").setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Monitor when "user to have job delivered to" flag changes in the database to trigger action
        DatabaseReference mAcceptDeclineNotification = mUserHandleRef.child("jobDeliveredToUID");
        mAcceptDeclineNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String s = dataSnapshot.getValue(String.class);
                if(!(s.equals("null"))){
                    ((GlobalVariables) getApplication()).setJobDeliveredToUID(s);
                    alertSnowplowNotification("Snow-Sense User Response", "User has accepted your offer. Click to open assignment window.","User has accepted your offer.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mSnowThreshHoldReached = mRootRef.child("users/" + mUser.getUid() + "/snowwarning/");
        mSnowThreshHoldReached.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SnowBuildupNotific notificParameters = dataSnapshot.getValue(SnowBuildupNotific.class);

                //If threshold flag has been raised AND the switch to allow threshold alerts, then
                if ((notificParameters.getSnowThresholdFlag() == 1) && (notificParameters.getEnableWarning() == 1)){
                    alertSensorOwnerOfSnowBuildup("Snow-Sense Sensor Alert", "Snow has accumulated to " + notificParameters.getSnowThreshold() + "in.", "Snow buildup alert!");
                    inAppAlert();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /**
         * Inefficient manipulation of FirebaseListAdapter to get data from data history (long nodes with many datapoints) in order to include timestamp, but I haven't found another way to do it
         */


        DatabaseReference mTimestampRef = mRootRef.child("sensors/" + mUser.getUid() + "/storeddata");
        mTimestampRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Sensors latestSensorData = dataSnapshot.getValue(Sensors.class);

                Date timeStampDate = new java.util.Date(latestSensorData.getTimestamp());
                String humanDate = new SimpleDateFormat("MM/dd/yyyy h:ma").format(timeStampDate);
                mLastUpdatedOn.setText("Last Updated: " + humanDate);
                mLastUpdatedOn.setTextColor(Color.BLACK);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        FirebaseListAdapter mAdapter = new FirebaseListAdapter<Sensors>(this, Sensors.class, android.R.layout.simple_list_item_2, databaseReference) {          //Listview using Sensors class
            @Override
            protected void populateView(View view, Sensors chatMessage, int position) {
                ((TextView) view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
                ((TextView) view.findViewById(android.R.id.text1)).setTextSize(18);
                ((TextView) view.findViewById(android.R.id.text2)).setText(chatMessage.getState());
                ((TextView) view.findViewById(android.R.id.text2)).setTextSize(16);

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

        if (id == R.id.action_drivewayphoto) {
            Intent drivewayPicture = new Intent(MainActivity.this, MyDrivewayPhoto.class);
            startActivity(drivewayPicture);
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


    /**
     * Area dedicated to notification experiment
     */

    public void alertSensorUserOfService (){

        //Set notification message contents
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setContentTitle("Snow-Sense Service Provider")
                .setContentText("Nearby snowplow has offered their services.")
                .setTicker("Service provider has offered their services.")
                .setAutoCancel(true)                                    //Automatically clear notification when clicked on the task bar
                .setSmallIcon(R.drawable.alreadyplowing);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);          //plays alert sound when notification is triggered

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

        notificationManager.notify(1, notificationBuilder.build());

        //Check whether it is currently on the notifcation window

//        notificationActive = true;
    }

    /**
     *Copy of "alertSensorUserOfService"
     */
    public void alertSnowplowNotification (String contentTitle, String contentText, String contentTicker){

        //Set notification message contents
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(contentTicker)
                .setAutoCancel(true)                                    //Automatically clear notification when clicked on the task bar
                .setSmallIcon(R.drawable.alreadyplowing);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        Intent mapsWindow = new Intent(this, MapsServiceMode.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MapsServiceMode.class);
        taskStackBuilder.addNextIntent(mapsWindow);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notificationBuilder.build());
    }

    /**
     *Copy of "alertSensorUserOfService"
     */
    public void alertSensorOwnerOfSnowBuildup (String contentTitle, String contentText, String contentTicker){

        //Set notification message contents
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(contentTicker)
                .setAutoCancel(true)                                    //Automatically clear notification when clicked on the task bar
                .setSmallIcon(R.drawable.alreadyplowing);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        Intent sensorUI = new Intent(this, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(sensorUI);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(3, notificationBuilder.build());
    }

    /**
     *Copy of "alertSensorUserOfService"
     */
    public void generalNotification (String contentTitle, String contentText, String contentTicker){

        //Set notification message contents
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new
                NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(contentTicker)
                .setAutoCancel(true)                                    //Automatically clear notification when clicked on the task bar
                .setSmallIcon(R.drawable.alreadyplowing);

        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(4, notificationBuilder.build());
    }

    /**
     * End of notifications
     */


    /**
     * Start of in-app alerts
     */

    public void inAppAlert (){
        final DatabaseReference mServiceRequestEnable = mRootRef.child("driveways/" + mUser.getUid());
        final DatabaseReference mSnowThresholdFlag = mRootRef.child("users/" + mUser.getUid() + "/snowwarning/");

        AlertDialog.Builder mAlert = new AlertDialog.Builder(this);
        mAlert.setMessage("Snow threshold reached. Allow snowplowers to detect you?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mServiceRequestEnable.child("status").setValue(2);
                        mSnowThresholdFlag.child("snowThresholdFlag").setValue(0);
                        notificationManager.cancel(3);                  //close notification regarding snow buildup
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mServiceRequestEnable.child("status").setValue(0);
                        mSnowThresholdFlag.child("snowThresholdFlag").setValue(0);
                        notificationManager.cancel(3);                  //close notification regarding snow buildup
                        dialog.dismiss();

                    }
                })
                .setNeutralButton("Disable Sensor Warnings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mSnowThresholdFlag.child("enableWarning").setValue(0);
                        mSnowThresholdFlag.child("snowThresholdFlag").setValue(0);
                        notificationManager.cancel(3);                  //close notification regarding snow buildup
                        dialog.dismiss();

                    }
                })

                .create();
        mAlert.show();
    }

    /**
     * End of in-app alerts
     */



//Back button won't close MainActivity
  /*  @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
    */
}
