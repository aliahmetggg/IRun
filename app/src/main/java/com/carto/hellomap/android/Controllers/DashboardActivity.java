package com.carto.hellomap.android.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.carto.hellomap.android.Helpers.SQLiteHelper;
import com.carto.hellomap.android.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class DashboardActivity extends Activity{

    TextView distanceDash, durationDash, totalActivities;
    Button newActivity, pastActivities, logout;
    ImageButton leaderboardBtn;

    SQLiteHelper db;
    SharedPreferences sharedPreferences;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Date totalTime;

    String userId;
    String totalDuration = "";

    long finalEpoch;
    long startEpoch;
    long finishEpoch;
    long totalEpoch = 0;
    double totalDistance = 0;
    int noOfActivities = 0;
    int totalHours = 0;
    int totalMinutes = 0;
    int totalSeconds = 0;

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );

        distanceDash = findViewById( R.id.distance_dashboard );
        durationDash = findViewById( R.id.duration_dashboard );
        newActivity =  findViewById( R.id.new_activity );
        pastActivities = findViewById( R.id.past_activities );
        logout = findViewById( R.id.logout_dash );
        totalActivities = findViewById( R.id.no_of_activity );
        leaderboardBtn = findViewById( R.id.leaderboard_button );

        db = new SQLiteHelper( DashboardActivity.this );
        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        userId = sharedPreferences.getString( "userId", "" );

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        storeDashboardData( userId );

        totalTime = new Date( totalEpoch);

        totalSeconds = totalTime.getSeconds();
        totalMinutes = totalTime.getMinutes();
        totalHours = totalTime.getHours();


        if( totalHours != 0){
            totalDuration = totalDuration + totalHours + " saat ";
        }

        if(  totalMinutes != 0){
            totalDuration = totalDuration + totalMinutes + " dakika ";
        }

        if(  totalSeconds != 0){
            totalDuration = totalDuration + totalSeconds + " saniye";
        }

        distanceDash.setText( String.valueOf( totalDistance  + " km") );
        durationDash.setText( totalDuration);
        totalActivities.setText( String.valueOf( noOfActivities ) );

        Task< Void > doc = fStore.collection( "Users" ).document(userId)
                .update( "TotalDistance", totalDistance );

        newActivity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( DashboardActivity.this, HelloMapActivity.class) );
            }
        } );

        pastActivities.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( DashboardActivity.this, PastActivitiesActivity.class ) );
            }
        } );

        leaderboardBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( DashboardActivity.this, LeaderboardActivity.class) );
            }
        } );

        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                fAuth.signOut();
                startActivity( new Intent( DashboardActivity.this, LoginActivity.class) );
            }
        } );

    }


    public void storeDashboardData( String userId){
        Cursor cursor = db.readData();

        if( cursor.getCount() == 0){
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()){

            String tempUserId = cursor.getString( 1 );

            if( tempUserId.equals( userId )){

                totalDistance = totalDistance + cursor.getInt( 2 );

                startEpoch = cursor.getLong(3);
                finishEpoch = cursor.getLong(4);
                finalEpoch = finishEpoch - startEpoch;
                totalEpoch = totalEpoch + finalEpoch;

                noOfActivities++;

            }
        }

    }
}
