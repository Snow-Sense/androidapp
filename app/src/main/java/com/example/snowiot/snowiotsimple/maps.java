package com.example.snowiot.snowiotsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class maps extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap drivewayMap;                                                                                   //necessary to be able to use google maps functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        FirebaseDatabase Database = FirebaseDatabase.getInstance();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);                    //"map" is fragment ID set
        mapFragment.getMapAsync(this);


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

                    if (driveway.getServiceRequest() == true) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                                                 .title(driveway.getName())
                                                                 .snippet(driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState()));              //Tutorial on this code by "GDD Recife" on youtube
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
