/*
 * Copyright (C) 2016 Google Inc.
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
package com.example.android.mytracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;





public class MainActivity extends Activity {

    private final String TAG = "MyTracker";
    private TextView mLocationView;

    private static final int NOTIFICATION_ID = 0;
    final long INTERVAL = 1000*60; //should be 1hour


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationView = new TextView(this);
        setContentView(mLocationView);
        mLocationView.setText("Location received: ------");

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerTime = SystemClock.elapsedRealtime() + INTERVAL;
        long repeatInterval =  + INTERVAL;
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                        (this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, repeatInterval, notifyPendingIntent);

        moveTaskToBack(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        startService(new Intent(getBaseContext(), GPSTracker.class));
    }


    @Override
    public void onStop(){
        super.onStop();
    }



}