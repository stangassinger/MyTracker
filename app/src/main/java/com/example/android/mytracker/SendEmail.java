package com.example.android.mytracker;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendEmail {
    private final String TAG = "SendEmail";

    SendEmail(Context context) {
        Log.i(TAG, "---->  .... SendEmail Constructor");
        try {
            GMailSender sender = new GMailSender("username@gmail.com", "password");
            sender.sendMail("This is Subject",
                    "This is Body",
                    "user@gmail.com",
                    "user@yahoo.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }


}
