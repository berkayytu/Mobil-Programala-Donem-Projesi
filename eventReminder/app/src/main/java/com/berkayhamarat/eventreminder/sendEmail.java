package com.berkayhamarat.eventreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class sendEmail extends AppCompatActivity {
    Button sendEmail;
    EditText emailTitle,emailReceiver,emailMessage;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        saveState = new SaveState(this);
        if(saveState.getState()== true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        sendEmail = findViewById(R.id.sendEmail);
        emailMessage = findViewById(R.id.emailMessage);
        emailReceiver = findViewById(R.id.emailReceiver);
        emailTitle = findViewById(R.id.emailTitle);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String detail = intent.getStringExtra("message");
        String address = intent.getStringExtra("address");

        emailTitle.setText(title);
        if(address==null){
            emailMessage.setText(detail);
        }else {
            emailMessage.setText(detail+"     Adres:"+address);
        }
    }


    public void sendEmail(View view) {

        try {
            String email = emailReceiver.getText().toString();
            String subject = emailTitle.getText().toString();
            String message = emailMessage.getText().toString();

            final Intent emailIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[]{email});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    subject);
            emailIntent
                    .putExtra(android.content.Intent.EXTRA_TEXT, message);
            this.startActivity(Intent.createChooser(emailIntent,
                    "Sending email..."));

        } catch (Throwable t) {
            Toast.makeText(this,
                    "Request failed try again: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}