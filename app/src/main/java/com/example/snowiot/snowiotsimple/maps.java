package com.example.snowiot.snowiotsimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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

public class maps extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

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

//        drivewayMap.setMyLocationEnabled(true);
        loadDrivewayLocations();

        drivewayMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.map_information_window, null);

                TextView markerName = (TextView) v.findViewById(R.id.infoName);
                TextView markerAddress = (TextView) v.findViewById(R.id.infoAddress);
                ImageView driveWayImage = (ImageView) v.findViewById(R.id.infoWindowPicture);

                Picasso.with(getApplicationContext()).load("https://static.concretenetwork.com/photo-gallery/images/1200x625Exact/concrete-driveways_8/stamped-concrete-driveway-d-e-contreras-construction_61246.jpg").into(driveWayImage); //pass image into imgview

                markerName.setText(marker.getTitle());
                markerAddress.setText(marker.getSnippet());

                return v;
            }
        });


//        drivewayMap.setOnMarkerClickListener(this);
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

                    if ((driveway.getServiceRequest() == true)&&(driveway.getType().equals("sensor"))&&(driveway.getStatus() == 1)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(driveway.getName())                                                                                       //get node name, which should be user UID
                                .snippet(driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())    //Tutorial on this code by "GDD Recife" on youtube
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.needsplowing)));
                    }

                    else if ((driveway.getServiceRequest() == true)&&(driveway.getType().equals("sensor"))&&(driveway.getStatus() == 2)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(driveway.getName())
                                .snippet(driveway.address.getStreet() + ", " + driveway.address.getCity() + ", " + driveway.address.getState())    //Tutorial on this code by "GDD Recife" on youtube
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.alreadyplowing)));
                    }

                    else if (driveway.getType().equals("plower") && (driveway.getStatus() == 1)) {
                        drivewayMap.addMarker(new MarkerOptions().position(new LatLng(driveway.getLatitude(), driveway.getLongitude()))
                                .title(dataSnapshot1.getKey())
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



}