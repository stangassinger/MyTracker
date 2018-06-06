package com.stangassinger.mytracker;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTracker extends Service  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {



    private final String TAG = "GPSTracker";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AlarmReceiver receiver;

    private static final int NOTIFICATION_ID = 0;
    final long INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES * 4; // should be one hour




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new AlarmReceiver();
        this.registerReceiver(receiver, filter);



        IntentFilter filterSh = new IntentFilter(Intent.ACTION_SHUTDOWN);
        this.registerReceiver(receiver, filterSh);


        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.i(TAG, "setInexactRepeating Alarm");
        long triggerTime = SystemClock.elapsedRealtime() + INTERVAL;
        long repeatInterval =  INTERVAL;
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                    (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime, repeatInterval, notifyPendingIntent);


        return START_STICKY;
    }






    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000000); // Update location every 16 minutes
        mLocationRequest.setFastestInterval(800000);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location){
        sendMessage(location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--------->    !!!!!!!! onDestroy  !!!! ");
    }

    private void sendMessage(Location location){
        Intent intent = new Intent("GPS_DATA");
        // You can also include some extra data.
        intent.putExtra("message_alt", location.getAltitude());
        intent.putExtra("message_lon", location.getLongitude());
        intent.putExtra("message_lat", location.getLatitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.i(TAG, "sendMessage " + location.getLongitude());
    }



}
