package com.example.android.mytracker;



import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTracker extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {



    private final String TAG = "MyAwesomeApp";


    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "---->  onStartCommand " );
        if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() ){
            // there is already a valid connection
        }else{
           mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
          mGoogleApiClient.connect();
        }
        return START_STICKY;
    }






    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "---->  onConnected " );

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(16000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "---->  GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "---->  GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location){
        Log.i(TAG, "---->  Location received: " + location.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "---->  onDestroy");
        //mGoogleApiClient.disconnect();
    }

}
