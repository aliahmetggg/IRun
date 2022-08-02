package com.carto.hellomap.android.Models;

public class User {

    String Email;
    String Name;
    String Surname;
    int TotalDistance;
    String Gender;

    public String getGender() {
        return Gender;
    }

    public void setGender( String gender ) {
        this.Gender = gender;
    }


    public User( String id, String name, String surname, int totalDistance,
                 String gender ) {
        this.Email = id;
        this.Name = name;
        this.Surname = surname;
        this.TotalDistance = totalDistance;
        this.Gender = gender;
    }

    public User(){}

    public void setEmail( String email ) {
        this.Email = email;
    }

    public void setName( String name ) {
        this.Name = name;
    }

    public void setSurname( String surname ) {
        this.Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }


    public void setTotalDistance( int totalDistance ) {
        this.TotalDistance = totalDistance;
    }

    public int getTotalDistance() {
        return TotalDistance;
    }

}
