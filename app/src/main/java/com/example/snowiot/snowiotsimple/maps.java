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

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class maps extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Button mContactSensorOwner;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    GoogleMap drivewayMap;                                                                                   //necessary to be able to use google maps functions
    Location userGPSLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        mContactSensorOwner = (Button) findViewById(R.id.startService);


        FirebaseDatabase Database = FirebaseDatabase.getInstance();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);                    //"map" is fragment ID set
        mapFragment.getMapAsync(this);

        /**
         * Call location based services from google. Framework needed to get location based services.
         */

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000); // maximum interval to request location, every minute
        mLocationRequest.setFastestInterval(5000); // minimum request interval is 10 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //Best accuracy and battery power balance


        mContactSensorOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent contactUser = new Intent(maps.this, ContactSensorOwner.class);
                startActivity(contactUser);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap drivewayMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        this.drivewayMap = drivewayMap;

        /*LatLng sydney = new LatLng(-33.852, 151.211);
        drivewayMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        drivewayMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)); */

        loadDrivewayLocations();

        DatabaseReference userTypeRef = FirebaseDatabase.getInstance().getReference("driveways/" + ((GlobalVariables) this.getApplication()).getUserUID() + "/type");

/**
 * Want to be able to synchronously get usertype from Firebase and create an IF statement here, so that the "mylocation" button will only appear if user is a snowplow user type.
 */
        drivewayMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        drivewayMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.map_information_window, null);

                TextView markerInfo = (TextView) v.findViewById(R.id.markerInfo);
                ImageView infoWindowPicture = (ImageView) v.findViewById(R.id.infoWindowPicture);

                Picasso.with(getApplicationContext()).load("http://i.imgur.com/ZMuAG6F.png").into(infoWindowPicture); //pass image into imgview

                markerInfo.setText(marker.getSnippet());

                ((GlobalVariables) getApplication()).setUserUIDFromMap(marker.getTitle());

                Toast.makeText(getApplicationContext(), "User information obtained. Proceed to 'services' window to begin.", Toast.LENGTH_SHORT).show();


                return v;
            }
        });

    }

    public void loadDrivewayLocations() {

        DatabaseReference drivewaysRef = FirebaseDatabase.getInstance().getReference("driveways");

        drivewaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                drivewayMap.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshots) {
                    Driveways driveway = dataSnapshot1.getValue(Driveways.class);

                    if ((driveway.getServiceRequest() == true) && (driveway.getType().equals("sensor")) && (driveway.getStatus() == 1)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(dataSnapshot1.getKey())                                                                                       //get node name, which should be user UID
                                .snippet(driveway.getName() + " at:" + driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())    //Tutorial on this code by "GDD Recife" on youtube
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));                                                                //Green means operating but not in need of service
                    } else if ((driveway.getServiceRequest() == true) && (driveway.getType().equals("sensor")) && (driveway.getStatus() == 2)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(dataSnapshot1.getKey())
                                .snippet(driveway.getName() + " at: " + driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));                                                                  //Red means operating and in need of service
                    } else if ((driveway.getServiceRequest() == true) && (driveway.getType().equals("sensor")) && (driveway.getStatus() == 3)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(dataSnapshot1.getKey())
                                .snippet(driveway.getName() + " at: " + driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));                                                                 //Blue means operating and already being serviced by a snowplow
                    } else if (driveway.getType().equals("plower") && (driveway.getStatus() == 1)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))                                 //Status: 0 = not on duty, 1 = on duty and standby, 2 = on duty but busy
                                .title(dataSnapshot1.getKey())
                                .snippet(driveway.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.plower)));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

//         Retrieve the data from the marker.
//        Integer clickCount = (Integer) marker.getTag();
//
//        // Check if a click count was set, then display the click count.
////        if (clickCount != null) {
////            clickCount = clickCount + 1;
////            marker.setTag(clickCount);
////            Toast.makeText(this,
////                    marker.getTitle() +
////                            " has been clicked " + clickCount + " times.",
////                    Toast.LENGTH_SHORT).show();
        Toast.makeText(this, marker.getTitle(),
                Toast.LENGTH_SHORT).show();


//        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (drivewayMap != null) {
            // Access to the location has been granted to the app.
            drivewayMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        DatabaseReference mSnowPlowInServiceCurrentLocation = FirebaseDatabase.getInstance().getReference("driveways/" + ((GlobalVariables) this.getApplication()).getUserUID());
        mSnowPlowInServiceCurrentLocation.child("latitude").setValue(location.getLatitude());
        mSnowPlowInServiceCurrentLocation.child("longitude").setValue(location.getLongitude());
//        snowPlowLatitude = location.getLatitude();
//        snowPlowLongitude = location.getLongitude();
    }

    //Necessary to stop tracking user when app is in the background
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

}