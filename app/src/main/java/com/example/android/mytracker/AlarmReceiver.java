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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver  {

    private static final int NOTIFICATION_ID = 0;
    private final String TAG = "AlarmReceiver";
    static double last_alt = 0;
    static double last_lon = 0;
    static double last_lat = 0;

    public AlarmReceiver() {

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            last_alt = intent.getDoubleExtra("message_alt",0);
            last_lon = intent.getDoubleExtra("message_lon",0);
            last_lat = intent.getDoubleExtra("message_lat",0);
            Log.i(TAG, "---->  .... XXXXXXXXXXXX:  alt:"+Double.toString(last_alt)+" lon: "+Double.toString(last_lon)+" lat: "+Double.toString(last_lat) );
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("GPS_DATA"));

        Log.i(TAG, "---->  ....  onReceive:  alt:"+Double.toString(last_alt)
                +" lon: "+Double.toString(last_lon)
                +" lat: "+Double.toString(last_lat) );

        //SendSMS sendSMS = new SendSMS();
        //sendSMS.sendSms("012345678", "TEST");


        String email = Config.SEND_TO;
        String subject = "smart-location";
        String message = "alt: " + Double.toString(last_alt) + "\n"
                +" lon: "+Double.toString(last_lon) + "\n"
                +" lat: "+Double.toString(last_lat);

        //Creating SendMail object
        SendMail sm = new SendMail(email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

}
