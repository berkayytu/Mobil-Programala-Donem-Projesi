package com.berkayhamarat.eventreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.berkayhamarat.eventreminder.MapsActivity.Address;
import static com.berkayhamarat.eventreminder.MapsActivity.cord1;
import static com.berkayhamarat.eventreminder.MapsActivity.cord2;

public class addEvent extends AppCompatActivity {
    TextView startDate,startTime,finalDate,finalTime,eventName,eventDetail,addAddress;
    Button setStartDate,setStartTime,setFinalDate,setFinalTime,create,takeAddress;
    SQLiteDatabase database;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        saveState = new SaveState(this);
        if(saveState.getState()== true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        finalDate = findViewById(R.id.finalDate);
        finalTime = findViewById(R.id.finalTime);
        eventName = findViewById(R.id.eventName);
        eventDetail = findViewById(R.id.eventDetail);
        addAddress = findViewById(R.id.address);
        setStartDate = findViewById(R.id.setStartDate);
        setStartTime = findViewById(R.id.setStartTime);
        setFinalDate = findViewById(R.id.setFinalDate);
        setFinalTime = findViewById(R.id.setFinalTime);
        create = findViewById(R.id.createEvent);
        takeAddress = findViewById(R.id.takeAddress);
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
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEvent();
            }
        });
        takeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });
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

    private void newEvent(){
        String eventN = eventName.getText().toString();
        String eventD = eventDetail.getText().toString();
        String startD = startDate.getText().toString();
        String startT = startTime.getText().toString();
        String finalD = finalDate.getText().toString();
        String finalT = finalTime.getText().toString();
        if (cord1 == null){
            cord1 = "";
        }
        if (cord2 == null){
            cord2 = "";
        }
        if (Address == null){
            Address = "";
        }

        try {
            database = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY,eventname VARCHAR, eventdetail VARCHAR, startdate VARCHAR, starttime VARCHAR, finaldate VARCHAR, finaltime VARCHAR,cord1 VARCHAR,cord2 VARCHAR,adres VARCHAR)");

            String sqlString = "INSERT INTO events (eventname, eventdetail, startdate, starttime, finaldate, finaltime,cord1,cord2,adres) VALUES (?, ?, ?, ?, ?, ?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,eventN);
            sqLiteStatement.bindString(2,eventD);
            sqLiteStatement.bindString(3,startD);
            sqLiteStatement.bindString(4,startT);
            sqLiteStatement.bindString(5,finalD);
            sqLiteStatement.bindString(6,finalT);
            sqLiteStatement.bindString(7,cord1);
            sqLiteStatement.bindString(8,cord2);
            sqLiteStatement.bindString(9,Address);
            sqLiteStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this,MainActivity.class);
        Toast toast = Toast.makeText(this, "Etkinlik başarı ile oluşturuldu", Toast.LENGTH_SHORT);
        toast.show();
        startActivity(intent);
    }
    private void getAddress(){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
        addAddress.setText(Address);
    }
}
