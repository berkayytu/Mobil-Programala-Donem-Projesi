package com.berkayhamarat.eventreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class reminderList extends AppCompatActivity {
    ListView listView;
    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;
    FloatingActionButton floatingActionButton;
    SQLiteDatabase database;
    SaveState saveState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        saveState = new SaveState(this);
        if(saveState.getState()== true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        listView = findViewById(R.id.list2);
        nameArray = new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameArray);
        listView.setAdapter(arrayAdapter);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = getIntent();
                int eventID = intent1.getIntExtra("eventID",1);
                Intent intent = new Intent(reminderList.this,reminderItem.class);
                intent.putExtra("reminderID",idArray.get(position));
                intent.putExtra("info",0);
                intent.putExtra("eventID",eventID);
                startActivity(intent);
            }
        });
        getData();

        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                int eventID = intent.getIntExtra("eventID",1);

                Intent intent1 = new Intent(reminderList.this,reminderItem.class);
                intent1.putExtra("info",1);
                intent1.putExtra("eventID",eventID);
                startActivity(intent1);
            }
        });
    }

    private void getData(){
        try {
            Intent intent = getIntent();
            int eventID = intent.getIntExtra("eventID",1);

            database = this.openOrCreateDatabase("reminders",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM reminders WHERE eventID =?", new String[] {String.valueOf(eventID)});
            int nameIx = cursor.getColumnIndex("date");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                nameArray.add(cursor.getString(nameIx));
                idArray.add(cursor.getInt(idIx));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
