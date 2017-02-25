package com.ethwillz.stinder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

public class MainScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    double lat, lng;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializes the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.changeClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Gets all users in database studying same class and plots on map
                mDatabase.child("onlineUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EditText subject = (EditText) findViewById(R.id.subject);

                        //Adds user to list of online students looking for partners
                        HashMap<String, String> userLocation = new HashMap<>();
                        userLocation.put("class", subject.getText().toString());
                        userLocation.put("lat", lat + "");
                        userLocation.put("lng", lng + "");
                        mDatabase.child("onlineUsers").child(""/*username*/).setValue(userLocation);

                        Iterable<DataSnapshot> usernames = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> user = usernames.iterator();

                        while(user.hasNext()) {
                            DataSnapshot entry = user.next();
                            double nextLat = Double.parseDouble(entry.child("lat").getValue().toString());
                            double nextLng = Double.parseDouble(entry.child("lng").getValue().toString());
                            //Checks if pin is not current user's and pin is within a certain distance of user
                            if (!entry.getKey().equals(""/*username of current user*/)
                                    && nextLat <= lat + .0005 && nextLat >= lat - .0005
                                    && nextLng <= lng + .0005 && nextLng >= lat - .0005) {
                                if (entry.child("class").getValue().toString().equals(subject.getText().toString())) {
                                    //Plot users lat and lng on map
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(nextLat, nextLng))
                                            .title(entry.getKey()));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Gets current location and sets camera to center and zoom on that location
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria,true);
            Location loc = locationManager.getLastKnownLocation(provider);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
        } else {
            // Show rationale and request permission.
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria,true);
            Location loc = locationManager.getLastKnownLocation(provider);
            lat = loc.getLatitude();
            lng = loc.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
        }
    }
}
