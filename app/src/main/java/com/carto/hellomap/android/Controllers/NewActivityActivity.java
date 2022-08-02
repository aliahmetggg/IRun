/*package com.carto.hellomap.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NewActivityActivity extends Activity{
    Button startStop;
    TextView currentDistance, currentDuration, totDistance, totDuration;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String date;
    boolean timerStart = false;
    Timer timer;
    TimerTask timerTask;
    double time = 0.0;
    SQLiteHelper db;
    SharedPreferences sharedPreferences;
    String userId;
    String finalDuration;
    String totalHours, totalMinutes, totalSeconds;
    String totalDuration = "";
    FirebaseFirestore fStore;

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_newactivity);

        currentDuration = ( TextView ) findViewById( R.id.duration_newact );
        startStop = (Button ) findViewById( R.id.start_newact );
        totDistance = (TextView ) findViewById( R.id.total_distance );
        totDuration = (TextView ) findViewById( R.id.total_duration );

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = simpleDateFormat.format( calendar.getTime() );

        timer = new Timer();
        db = new SQLiteHelper( NewActivityActivity.this );

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        userId = sharedPreferences.getString( "userId", "" );

        startStop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startTimer();
            }
        } );

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    public void startTimer(){
        if( !timerStart){
            timerStart = true;
            startStop.setText( "Stop" );
            startCountUp();
        }
        else{
            timerStart = false;
            startStop.setText( "Start" );
            timerTask.cancel();
            finalDuration = currentDuration.getText().toString();

            totalHours = finalDuration.substring( 0, 2 );
            totalMinutes = finalDuration.substring( 3, 5 );
            totalSeconds = finalDuration.substring( 6 );

            int hours = Integer.parseInt( totalHours );
            int minutes = Integer.parseInt( totalMinutes );
            int seconds = Integer.parseInt( totalSeconds );

            if( !totalHours.equals("00")){
                totalDuration = totalDuration + totalHours + " saat ";
            }

            if( ! totalMinutes.equals( "00" )){
                totalDuration = totalDuration + totalMinutes + " dakika ";
            }

            if( ! totalSeconds.equals( "00" )){
                totalDuration = totalDuration + totalSeconds + " saniye";
            }

            currentDuration.setText( totalDuration );
            totDuration.setText( "Toplam Süre:" );
            totDistance.setText( "Toplam mesafe:" );

            db.addNewActivity( userId, 50, finalDuration, date, hours, minutes, seconds );

        }
    }

    public void startCountUp() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( new Runnable(){
                    @Override
                    public void run() {
                        time++;
                        currentDuration.setText( getTimerText() );
                    }

                });
            }
        };
        timer.scheduleAtFixedRate( timerTask, 0, 1050 );
    }

    public String getTimerText(){
        int rounded = (int) Math.round( time );

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime( seconds, minutes, hours);

    }

    public String formatTime( int seconds, int minutes, int hours ) {

        return String.format( "%02d", hours ) + ":" + String.format( "%02d", minutes ) + ":" + String.format( "%02d", seconds );
    }
}*/
