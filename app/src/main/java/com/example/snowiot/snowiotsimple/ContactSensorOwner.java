package com.example.snowiot.snowiotsimple;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ContactSensorOwner extends AppCompatActivity {

    private Button mSendRequest;
    private TextView mUserName;
    private TextView mUserAddress;
    private TextView mSnowAccumulation;
    private ImageView mDrivewayPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sensor_owner);


        mSendRequest = (Button) findViewById(R.id.sendJobRequest);
        mUserName = (TextView) findViewById(R.id.contactName);
        mUserAddress = (TextView) findViewById(R.id.contactAddress);
        mSnowAccumulation = (TextView) findViewById(R.id.contactSnowAccumulation);
        mDrivewayPhoto = (ImageView) findViewById(R.id.contactDrivewayPhoto);

    }
}
