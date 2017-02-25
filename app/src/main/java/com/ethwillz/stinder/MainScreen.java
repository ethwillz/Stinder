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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class MainScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializes the database
        mDatabase = new FirebaseDatabase().getInstance().getReference();

        findViewById(R.id.changeClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Gets all users in database studying same class and plots on map
                mDatabase.child("onlineUsers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EditText subject = (EditText) findViewById(R.id.subject);
                        Iterable<DataSnapshot> usernames = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> user = usernames.iterator();

                        while(user.hasNext()){
                            if(user.next().child("class").getValue().toString().equals(subject.getText().toString())){
                                //Plot users lat and lng on map
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(user.next().child("lat").getValue().toString()), Double.parseDouble(user.next().child("lng").getValue().toString())))
                                        .title(user.next().getKey()));
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
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
        }
    }
}
