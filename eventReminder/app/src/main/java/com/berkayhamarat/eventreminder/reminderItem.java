package com.berkayhamarat.eventreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.google.android.material.snackbar.Snackbar;

public class reminderItem extends AppCompatActivity {
    TextView reminderdate,remindertime;
    Button setDate,setTime,addReminder,deleteReminder;
    SQLiteDatabase database,database2;
    int alarmDate,alarmYear,alarmMonth,alarmHour,alarmMin,notificationID;
    String eventN,eventD;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_item);

        saveState = new SaveState(this);
        if(saveState.getState()== true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        reminderdate = findViewById(R.id.reminderDate);
        remindertime = findViewById(R.id.reminderTime);
        setDate = findViewById(R.id.setRDate);
        setTime = findViewById(R.id.setRTime);
        addReminder = findViewById(R.id.createReminder);
        deleteReminder = findViewById(R.id.deleteReminder);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RDate();
            }
        });
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RTime();
            }
        });

        Intent intent = getIntent();
        int info = intent.getIntExtra("info",1);
        if (info == 1){
            addReminder.setVisibility(View.VISIBLE);
            reminderdate.setText("");
            remindertime.setText("");
        }else {
            addReminder.setVisibility(View.INVISIBLE);

            Intent intent1 = getIntent();
            int reminderID = intent1.getIntExtra("reminderID",1);
            try {
                database = this.openOrCreateDatabase("reminders",MODE_PRIVATE,null);
                Cursor cursor = database.rawQuery("SELECT * FROM reminders WHERE id = ?",new String[] {String.valueOf(reminderID)});

                int dateI = cursor.getColumnIndex("date");
                int timeI = cursor.getColumnIndex("time");
                while (cursor.moveToNext()) {
                    reminderdate.setText(cursor.getString(dateI));
                    remindertime.setText(cursor.getString(timeI));
                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createR();
            }
        });
        deleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(v,"Hatırlatıcı kaydı silinsin mi ?",Snackbar.LENGTH_LONG)
                        .setAction("Sil!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteR();
                            }
                        });
                snackbar.show();
            }
        });
    }

    private void RDate(){
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                String dateString = date+" "+month+" "+year;
                alarmDate = date;
                alarmMonth = month;
                alarmYear = year;
                reminderdate.setText(dateString);
            }
        },YEAR,MONTH,DATE);
        datePickerDialog.show();
    }
    private void RTime(){
        final Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24HourFormat = DateFormat.is24HourFormat(this);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String timeString = hour+":"+minute;
                alarmHour = hour;
                alarmMin = minute;
                remindertime.setText(timeString);
            }
        },HOUR,MINUTE,is24HourFormat);
        timePickerDialog.show();
    }

    private void createR(){
        Intent intent = getIntent();
        int eventID = intent.getIntExtra("eventID",1);

        String newDate = reminderdate.getText().toString();
        String newTime = remindertime.getText().toString();
        try {
            database = this.openOrCreateDatabase("reminders",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS reminders (id INTEGER PRIMARY KEY,eventID INTEGER,date VARCHAR,time VARCHAR)");
            String sqlString = "INSERT INTO reminders (eventID,date,time) VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,String.valueOf(eventID));
            sqLiteStatement.bindString(2,newDate);
            sqLiteStatement.bindString(3,newTime);
            sqLiteStatement.execute();
            Toast.makeText(this,"Hatırlatıcı Oluşturuldu",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
        createAlarm();
        Intent intent1 = new Intent(this,MainActivity.class);
        intent1.putExtra("eventID",eventID);
        startActivity(intent1);
    }
    private void deleteR(){
        Intent intent = getIntent();
        int reminderID = intent.getIntExtra("reminderID",1);
        database.delete("reminders","id=?",new String[] {String.valueOf(reminderID)});
        Toast.makeText(this,"Silme işkemi başarılı",Toast.LENGTH_SHORT).show();
        deleteAlarm();
        Intent intent1 = new Intent(this,MainActivity.class);
        startActivity(intent1);
    }
    private void createAlarm(){

        Intent event = getIntent();
        int eventID = event.getIntExtra("eventID",1);

        Cursor cursor = database.rawQuery("SELECT * FROM reminders WHERE eventID = ? ORDER BY id DESC LIMIT 1",new String[] {String.valueOf(eventID)});
        int idI = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            notificationID = cursor.getInt(idI);
        }
        cursor.close();

        database2 = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);
        Cursor cursor2 = database.rawQuery("SELECT * FROM events WHERE id = ?",new String[] {String.valueOf(eventID)});
        int eName = cursor2.getColumnIndex("eventname");
        int eDetail = cursor2.getColumnIndex("eventdetail");
        while (cursor2.moveToNext()) {
            eventN = cursor2.getString(eName);
            eventD = cursor2.getString(eDetail);
        }
        cursor2.close();


        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("notificationID",notificationID);
        intent.putExtra("title",eventN);
        intent.putExtra("detail",eventD);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH,alarmDate);
        startTime.set(Calendar.MONTH,alarmMonth);
        startTime.set(Calendar.YEAR,alarmYear);
        startTime.set(Calendar.HOUR,alarmHour);
        startTime.set(Calendar.MINUTE,alarmMin);
        long alarmStartTime = startTime.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,alarmStartTime,pendingIntent);

    }

    private void deleteAlarm(){
        Intent event = getIntent();
        int eventID = event.getIntExtra("eventID",1);

        database = this.openOrCreateDatabase("reminders",MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("SELECT * FROM reminders WHERE id = ? ORDER BY id DESC LIMIT 1",new String[] {String.valueOf(eventID)});
        int idI = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            notificationID = cursor.getInt(idI);
        }
        cursor.close();

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("notificationID",notificationID);
        intent.putExtra("title",eventN);
        intent.putExtra("detail",eventD);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }
}
