package com.example.android.mytracker;

import android.telephony.SmsManager;


public class SendSMS {


    public void sendSms(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}