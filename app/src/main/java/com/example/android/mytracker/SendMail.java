package com.example.android.mytracker;

import javax.mail.Session;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMail extends AsyncTask<Void,Void,Void> {

    private final String TAG = "SendMail";
    private boolean task_finished      = false;
    private boolean mailsendsuccess    = true;

    //Declaring Variables

    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;

    //Class Constructor
    public SendMail( String email, String subject, String message){
        //Initializing variables
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        task_finished = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "mail.gmx.net");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(Config.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            mailsendsuccess = false;
            Log.i(TAG, "Error: Sending email failed !!!" );
            e.printStackTrace();
        }
        return null;
    }

    public boolean isFinished(){
        return task_finished;
    }

    public boolean getmailSendSuccess(){
        return mailsendsuccess;
        //return true;
    }

}