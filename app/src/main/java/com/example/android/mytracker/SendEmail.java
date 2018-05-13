package com.example.android.mytracker;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendEmail {
    private final String TAG = "SendEmail";

    SendEmail(Context context) {
        Log.i(TAG, "---->  .... SendEmail Constructor");
        String email = "guenther.stangassinger@gmx.de";
        String subject = "email subject";
        String message = "email message";

        //Creating SendMail object
        SendMail sm = new SendMail(email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }


}
