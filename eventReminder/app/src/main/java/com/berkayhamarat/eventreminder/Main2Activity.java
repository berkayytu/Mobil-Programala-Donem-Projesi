package com.berkayhamarat.eventreminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.berkayhamarat.eventreminder.MapsActivity.Address;

public class Main2Activity extends AppCompatActivity {
    TextView startDate,startTime,finalDate,finalTime,eventName,eventDetail,address;
    Button setStartDate,setStartTime,setFinalDate,setFinalTime,saveEvent,deleteEvent,addAddress,sendE,reminders;
    SQLiteDatabase database;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        saveState = new SaveState(this);
        if(saveState.getState()== true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        startDate = findViewById(R.id.startDate2);
        startTime = findViewById(R.id.startTime2);
        finalDate = findViewById(R.id.finalDate2);
        finalTime = findViewById(R.id.finalTime2);
        eventName = findViewById(R.id.eventName2);
        eventDetail = findViewById(R.id.eventDetail2);
        address = findViewById(R.id.address2);
        setStartDate = findViewById(R.id.setStartDate2);
        setStartTime = findViewById(R.id.setStartTime2);
        setFinalDate = findViewById(R.id.setFinalDate2);
        setFinalTime = findViewById(R.id.setFinalTime2);
        saveEvent = findViewById(R.id.saveEvent);
        deleteEvent = findViewById(R.id.deleteEvent);
        addAddress = findViewById(R.id.takeAddress2);
        sendE = findViewById(R.id.sendE);
        reminders = findViewById(R.id.reminder);
        setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSDate();
            }
        });
        setFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFDate();
            }
        });
        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSTime();
            }
        });
        setFinalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFTime();
            }
        });

        reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remind();
            }
        });
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(v,"Etkinlik kaydı güncellensin mi ?",Snackbar.LENGTH_LONG)
                        .setAction("Güncelle!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save(v);
                            }
                        });
                snackbar.show();
            }
        });
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(v,"Etkinlik kaydı silinsin mi ?",Snackbar.LENGTH_LONG)
                        .setAction("Sil!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteE();
                            }
                        });
                snackbar.show();
            }
        });
        sendE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });

        database = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);

        Intent intent = getIntent();

        int eventID = intent.getIntExtra("eventID",1);

        try {
            Cursor cursor = database.rawQuery("SELECT * FROM events WHERE id = ?",new String[] {String.valueOf(eventID)});
            int eName = cursor.getColumnIndex("eventname");
            int eDetail = cursor.getColumnIndex("eventdetail");
            int startD = cursor.getColumnIndex("startdate");
            int startT = cursor.getColumnIndex("starttime");
            int finalD = cursor.getColumnIndex("finaldate");
            int finalT = cursor.getColumnIndex("finaltime");
            int addressI = cursor.getColumnIndex("adres");
            while (cursor.moveToNext()) {
                eventName.setText(cursor.getString(eName));
                eventDetail.setText(cursor.getString(eDetail));
                startDate.setText(cursor.getString(startD));
                startTime.setText(cursor.getString(startT));
                finalDate.setText(cursor.getString(finalD));
                finalTime.setText(cursor.getString(finalT));
                address.setText(cursor.getString(addressI));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteE() {
        Intent intent = getIntent();
        int eventID = intent.getIntExtra("eventID",1);
        database.delete("events","id=?",new String[] {String.valueOf(eventID)});
        Toast.makeText(this,"Silme işkemi başarılı",Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(this,MainActivity.class);
        startActivity(intent1);
    }

    public void save(View view) {
        Intent intent = getIntent();
        int eventID = intent.getIntExtra("eventID",1);
        String eventN = eventName.getText().toString();
        String eventD = eventDetail.getText().toString();
        String startD = startDate.getText().toString();
        String startT = startTime.getText().toString();
        String finalD = finalDate.getText().toString();
        String finalT = finalTime.getText().toString();
        String newAdress = address.getText().toString();
        String id = String.valueOf(eventID);
        try {
            database = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);
            ContentValues values = new ContentValues();
            values.put("eventname",eventN);
            values.put("eventdetail",eventD);
            values.put("startdate",startD);
            values.put("starttime",startT);
            values.put("finaldate",finalD);
            values.put("finaltime",finalT);
            values.put("adres",newAdress);
            database.update("Events",values,"id="+id,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent1 = new Intent(Main2Activity.this,MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        //finish();
    }
    private void setSDate(){
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                String dateString = date+" "+month+" "+year;
                startDate.setText(dateString);
            }
        },YEAR,MONTH,DATE);
        datePickerDialog.show();
    }
    private void setFDate(){
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                String dateString = date+" "+month+" "+year;
                finalDate.setText(dateString);
            }
        },YEAR,MONTH,DATE);
        datePickerDialog.show();
    }
    private void setSTime(){
        final Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String timeString = hour+":"+minute;
                startTime.setText(timeString);
            }
        },HOUR,MINUTE,is24HourFormat);
        timePickerDialog.show();
    }
    private void setFTime(){
        final Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String timeString = hour+":"+minute;
                finalTime.setText(timeString);
            }
        },HOUR,MINUTE,is24HourFormat);
        timePickerDialog.show();
    }
    private void getAddress(){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
        address.setText(Address);
    }
    private void sendMail(){
        Intent intent =new Intent(this,sendEmail.class);
        intent.putExtra("title",eventName.getText().toString());
        intent.putExtra("message",eventDetail.getText().toString());
        intent.putExtra("address",address.getText().toString());
        startActivity(intent);
    }
    private void remind(){
        Intent intent = getIntent();
        int eventID = intent.getIntExtra("eventID",1);
        Intent intent1 = new Intent(this,reminderList.class);
        intent1.putExtra("eventID",eventID);
        startActivity(intent1);
    }
}
