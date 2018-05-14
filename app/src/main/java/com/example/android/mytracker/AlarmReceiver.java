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

    // Strategy for sending notifications
    //Wait for 23 hours to finally send notification
    static int waiting_count = 0;
    // Wait another 23 hours where notification is sent as soon as possible
    static int try_to_send_count = 0;
    // After that time send SMS notification
    final int COUNTING_TARGET = 4 ; // should be 23 at the final version


    public AlarmReceiver() {

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            last_alt = intent.getDoubleExtra("message_alt",0);
            last_lon = intent.getDoubleExtra("message_lon",0);
            last_lat = intent.getDoubleExtra("message_lat",0);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        waiting_count++;
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("GPS_DATA"));


        //SendSMS sendSMS = new SendSMS();
        //sendSMS.sendSms("012345678", "TEST");

        if ( waiting_count == COUNTING_TARGET) {

            waiting_count = 0;
            //sendMailNotification();
        }

    }

    private void sendMailNotification(){
        String email = Config.SEND_TO;
        String subject = "smart-location";
        String message = "alt: " + Double.toString(last_alt) + "\n"
                + Double.toString(last_lat) + ","+Double.toString(last_lon);
        SendMail sm = new SendMail(email, subject, message);
        sm.execute();
    }


}
