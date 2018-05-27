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
package com.stangassinger.mytracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver  {

    private static final int NOTIFICATION_ID = 0;
    private final String TAG = "AlarmReceiver";
    static double last_alt = 0;
    static double last_lon = 0;
    static double last_lat = 0;

    static boolean network_connection_on = false;

    static boolean must_send_email_immediately = false;

    // Strategy for sending notifications
    //Wait for COUNTING_TARGET hours to finally send notification
    static int waiting_count = 0;
    // Wait another COUNTING_TARGET hours where notification is sent as soon as possible
    // After that time send SMS notification
    final int COUNTING_TARGET       = 4 ;
    final int UPPER_COUNTING_TARGET = 24;

    public AlarmReceiver() {

    }

    private void waitForSpezialSmsMessage(String message, String phone_number){
        if (message.equals(new String("alarm")) ) {
            sending_sms(phone_number   , "lrm");
            sending_sms(Config.PHONE_NR, "lrm");
        }
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

    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i(TAG, "senderNum: "+ senderNum + "; message: " + message);
                    waitForSpezialSmsMessage(message, senderNum);

                }
            }

        } catch (Exception e) {
        }




        if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())){
            Log.i(TAG, "---->   SHUTDOWN  !!!!!!" + intent.getAction());
            sending_sms(Config.PHONE_NR, "sht_dwn");
            return;
        }



        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())){
            Log.i(TAG, "---->xxxxxxx: " + intent.getAction());
            Log.i(TAG, "---->  .... Battery LOW!!!");
            sending_sms(Config.PHONE_NR, "bt_low");
            return;
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE )) {
                network_connection_on = true;
                if (must_send_email_immediately){
                    mail_send_routine();
                }
                Log.i(TAG, "---->  .... Network is connected");
            } else {
                network_connection_on = false;
                Log.i(TAG, "---->  .... No active connection");
            }
        }else{
            waiting_count++;
            LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                    new IntentFilter("GPS_DATA"));

            Log.i(TAG, "." + waiting_count);
            if ( waiting_count == COUNTING_TARGET) {
                Log.i(TAG, "--------> waiting_count: " + waiting_count);
                mail_send_routine();
            }


        }

        if (must_send_email_immediately == true){
            if (waiting_count >= COUNTING_TARGET && waiting_count < UPPER_COUNTING_TARGET) {
                Log.i(TAG, "----www----> waiting_count: " + waiting_count);
                mail_send_routine();
            }else{
                if (waiting_count >= UPPER_COUNTING_TARGET) {
                    sending_sms(Config.PHONE_NR, "no_net");
                }
            }
        }
    }

    private void mail_send_routine(){
        if ( network_connection_on == true) {
            if (sendMailNotification() == true) {
                must_send_email_immediately = false;
                waiting_count = 0;
                return;
            }
        }
        must_send_email_immediately = true;
    }

    private boolean sendMailNotification(){
        boolean ret = false;
        String email = Config.SEND_TO;
        String subject = "smart-location";
        String message = "alt: " + Double.toString(last_alt) + "\n"
                + Double.toString(last_lat) + ","+Double.toString(last_lon);
        SendMail sm = new SendMail(email, subject, message);
        sm.execute();
        int i = 0;
        while (sm.isFinished() == false ) {
            i++;
            if (i > 5){ break;}
            try {
                Thread.sleep(1000);
            } catch (Exception e) {Log.i(TAG, "------error--> WAITING");}
        }
        if (sm.getmailSendSuccess() ){
            Log.i(TAG, "--------> Sending Mail Successfull");
            ret = true;
        }else{
            Log.i(TAG, "--------> Sending Mail Failed !!!!!");
            ret = false;
        }
        return ret;
    }

    private void sending_sms(String phone_nr, String pre_message){
        Log.i(TAG, "--------> Sending SMS");
        String message = "alt: " + Double.toString(last_alt) + "\n"
                + Double.toString(last_lat) + ","+Double.toString(last_lon);
        waiting_count = 0;
        SendSMS sendSMS = new SendSMS();
        sendSMS.sendSms(phone_nr, pre_message + "\n" + message);
    }


}
