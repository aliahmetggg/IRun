package com.carto.hellomap.android.Models;

public class Activity {

    String duration;
    int distance;
    String date;
    String userId;

    public Activity( int distance, String duration, String date, String userId){
        this.duration = duration;
        this.distance = distance;
        this.date = date;
        this.userId = userId;
    }


    public String getDuration(){
        return duration;
    }

    public int getDistance(){
        return distance;
    }

    public String getDate(){
        return date;
    }

    String getUserId(){
        return userId;
    }

}
