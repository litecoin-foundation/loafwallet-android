package com.breadwallet.presenter.activities.settings;


import android.Manifest;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import com.breadwallet.R;
import com.breadwallet.presenter.entities.BRBusinessEntity;
import com.breadwallet.tools.animation.BRAnimator;
import com.breadwallet.tools.sqlite.BusinessesDataSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final String TAG = "GPS";

    private static final double PADDING_PERCENTAGE = 0.12;// offset from edges of the map 12% of screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        else
        if (askPermission())
            mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        BusinessesDataSource bds;
        bds = BusinessesDataSource.getInstance(this);
        List<BRBusinessEntity> businesses = bds.getAllBusinesses();
        Iterator iterator = businesses.iterator();

//        Toast toast = Toast.makeText(this, "Loading " + String.valueOf(businesses.size()) + "\nbusinesses", Toast.LENGTH_LONG);
//        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//        if (v != null) v.setGravity(Gravity.CENTER);
//        toast.show();

        Calendar toDayCalendar =Calendar.getInstance();
        Date currentDate=toDayCalendar.getTime(); //today

//        Toast.makeText(this, String.valueOf(currentDate), Toast.LENGTH_LONG).show();

        while (iterator.hasNext()) {
            BRBusinessEntity business = (BRBusinessEntity) iterator.next();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            Calendar expiryCalendar = Calendar.getInstance();
            try {
                expiryCalendar.setTime(sdf.parse(business.getdateStart())); //this will grab the date from the startDate from the database, also use 00:00:00 as the time
            } catch (ParseException e) {
                e.printStackTrace();
            }
            expiryCalendar.add(Calendar.DATE,business.getregLength()+1);  //up to midnight of the day of expiry, since time is not used in startDate, but is in currentDate
            Date expiryDate=expiryCalendar.getTime();  //expiry date of business registration

            if(currentDate.compareTo(expiryDate)<=0){ // when currentDate is lower to or equal to the expiryDate
                LatLng NAHLocation = new LatLng((double) business.getlat(), (double) business.getlng());
                mMap.addMarker(new MarkerOptions().position(NAHLocation).title(business.getbusinessname()).snippet(business.getbusinessproducts()).icon(BitmapDescriptorFactory.fromResource(R.drawable.strayacoin_32)));
            }
            ;
        }

        // Create a LatLngBounds that includes Australia.

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * PADDING_PERCENTAGE);

        LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(-44, 113), new LatLng(-10, 154));

        // Set the camera to the greatest possible zoom level that includes the
        // bounds
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, width, height, padding));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (!BRAnimator.isClickAllowed()) return;
        BRAnimator.showRegisterBusinessFragment(MapsActivity.this, "SUuRv3YopiymMAJ4NrCEJPzk2Q3nXZVyRe", latitude, longitude);
//        Toast.makeText(this, "Current Lat:" + latitude + "\nCurrent Lng:" + longitude, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private boolean askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_CODE_ASK_PERMISSIONS
        );
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkPermission())
                        mMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied

                }
                break;
            }
        }
    }
}

