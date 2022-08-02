package com.carto.hellomap.android.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carto.hellomap.android.Adapters.CustomAdapter;
import com.carto.hellomap.android.Helpers.SQLiteHelper;
import com.carto.hellomap.android.Models.Activity;
import com.carto.hellomap.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PastActivitiesActivity extends android.app.Activity {

    ArrayList< Activity > activities;
    SQLiteHelper db;
    String currentUserId;
    SharedPreferences sharedPreferences;
    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    SimpleDateFormat simpleDateFormatTime;
    SimpleDateFormat simpleDateFormatDate;
    String duration;
    String date;


    public void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.recycler_pastactivities );

        recyclerView = findViewById( R.id.recyclerView );

        activities = new ArrayList< Activity >();
        db = new SQLiteHelper( PastActivitiesActivity.this );

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        currentUserId = sharedPreferences.getString( "userId", "" );

        simpleDateFormatTime = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormatDate = new SimpleDateFormat("MM/dd/yyyy");

        storeData( currentUserId);

        customAdapter = new CustomAdapter( PastActivitiesActivity.this, activities);
        recyclerView.setAdapter( customAdapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( PastActivitiesActivity.this ) );


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    public void storeData( String userId){
        Cursor cursor = db.readData();
        Activity newActivity;
        Date newDate;
        Date tempDate;

        if( cursor.getCount() == 0){
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()){

            String tempUserId = cursor.getString( 1 );

            if( tempUserId.equals( currentUserId )){

                newDate = new Date( cursor.getLong( 4 ) - cursor.getLong( 3 ));
                tempDate = new Date( cursor.getLong( 3 ));

                duration = simpleDateFormatTime.format( newDate );
                date = simpleDateFormatDate.format( tempDate );
                
                newActivity = new Activity( cursor.getInt(2), duration, date, tempUserId );
                activities.add( newActivity);
            }
        }

    }
}
