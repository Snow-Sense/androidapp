/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.snowiot.snowiotsimple;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsServiceMode extends AppCompatActivity implements
        OnMapReadyCallback{

    GoogleMap drivewayMap;                                                                                   //necessary to be able to use google maps functions


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_service_mode);


        FirebaseDatabase Database = FirebaseDatabase.getInstance();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapServiceMode);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap drivewayMap) {

        this.drivewayMap = drivewayMap;

       if(!(((GlobalVariables) getApplication()).getJobDeliveredToUID().equals(null))){
           loadUserLocation();
       }

        DatabaseReference userTypeRef = FirebaseDatabase.getInstance().getReference("driveways/" + ((GlobalVariables) this.getApplication()).getUserUID() + "/type");


    }

    public void loadUserLocation() {

        DatabaseReference drivewaysRef = FirebaseDatabase.getInstance().getReference("driveways");

        drivewaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                drivewayMap.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshots) {
                    Driveways driveway = dataSnapshot1.getValue(Driveways.class);


                    if ((dataSnapshot1.getKey()).equals(((GlobalVariables) getApplication()).getJobDeliveredToUID())){
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title("Sensor Owner")
                                .snippet(driveway.getName() + " at:" + driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        drivewayMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(driveway.getLatitude(), driveway.getLongitude()), 15));


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    }



