package com.ethwillz.stinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainScreen extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    double lat, lng;

    LinearLayout mainLayout;
    FirebaseUser user;
    BottomSheetBehavior mBottomSheetBehavior;
    Marker curMarker;
    String username, provider;
    LocationManager locationManager;
    EditText subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mainLayout = new LinearLayout(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        mBottomSheetBehavior.setState(mBottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior.from(findViewById(R.id.bottomSheet)).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        curMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // no op
            }
        });

        //Initializes the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        subject = (EditText) findViewById(R.id.subject);
        subject.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(subject.getWindowToken(), 0);

                    mDatabase.child("users").child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            username = dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    //Gets all users in database studying same class and plots on map
                    mDatabase.child("onlineUsers").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mMap.clear(); //Clears all pins from old class
                            EditText subject = (EditText) findViewById(R.id.subject);

                            //Adds user to list of online students looking for partners
                            HashMap<String, String> userLocation = new HashMap<>();
                            userLocation.put("username", username);
                            userLocation.put("class", subject.getText().toString());
                            userLocation.put("lat", lat + "");
                            userLocation.put("lng", lng + "");
                            //Uncomment out user.getUid() when authentication set up
                            mDatabase.child("onlineUsers").child(user.getUid()).setValue(userLocation);

                            //Runs while there are more users in table
                            for (DataSnapshot entry : dataSnapshot.getChildren()) {
                                double nextLat = Double.parseDouble(entry.child("lat").getValue().toString());
                                double nextLng = Double.parseDouble(entry.child("lng").getValue().toString());

                                //Checks if pin is not current user's and pin is within a certain distance of user
                                if (!entry.getKey().equals(user.getUid())
                                    /*&& nextLat <= lat + .01 && nextLat >= lat - .01
                                    && nextLng <= lng + .01 && nextLng >= lat - .01*/) {
                                    if (entry.child("class").getValue().toString().equals(subject.getText().toString())
                                            && nextLat < lat + .1 && nextLat > lat - .1 && nextLng < lng + .1 && nextLng > lng - .1) {
                                        //Plot users lat and lng on map
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(nextLat, nextLng))
                                                .title(entry.getKey())
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
                return false;
            }
        });

        //Handles opening new chat with another user
        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String uid1 = user.getUid();
                //String uid2 =
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Gets current location and sets camera to center and zoom on that location
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria,true);
            Location loc = locationManager.getLastKnownLocation(provider);
            lat = loc.getLatitude();
            lng = loc.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
        } else {
            // Show rationale and request permission.
            String[] PERMISSION_STORAGE = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            };

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSION_STORAGE,
                    1
            );
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker){

        mDatabase.child("users").child(marker.getTitle()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView name = (TextView) findViewById(R.id.userInfoName);
                name.setText(dataSnapshot.child("name").getValue().toString());
                TextView username = (TextView) findViewById(R.id.userInfoUsername);
                username.setText(dataSnapshot.child("username").getValue().toString());
                TextView major = (TextView) findViewById(R.id.userInfoMajor);
                major.setText(dataSnapshot.child("major").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        curMarker = marker;
        //Sets icon to red
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        return true;
    }

    //TODO Add option to remove yourself from online users so the app doesn't have to be destoryed and you can be messaged more efefctively
    @Override
    public void onDestroy(){
        //Removes user from online users section of database upon exiting application
        //TODO Move this to a static method in seperate class & reference in every activity
        System.out.println("Exit method running");
        mDatabase.child("onlineUsers").child(user.getUid()).removeValue();
        super.onDestroy();
    }

    //Doesn't do anything when back button pressed
    @Override
    public void onBackPressed(){

    }
/*
    @Override
    public void onPause(){
        //Removes user from online users section of database upon exiting application
        //TODO Move this to a static method in seperate class & reference in every activity
        System.out.println("Exit method running");
        mDatabase.child("onlineUsers").child(user.getUid()).removeValue();
        super.onPause();
    }*/
}
