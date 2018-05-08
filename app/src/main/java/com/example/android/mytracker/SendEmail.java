package com.example.android.mytracker;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendEmail {
    private final String TAG = "SendEmail";

    SendEmail(Context context) {
        Log.i(TAG, "---->  .... SendEmail Constructor");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL,"info@example.cin");
        i.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        i.putExtra(Intent.EXTRA_TEXT, "body mail");
        context.startActivity(i);
    }


}
