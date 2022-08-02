package com.carto.hellomap.android.Controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

import com.carto.hellomap.android.Controllers.HelloMapActivity;
import com.carto.hellomap.android.R;


public class MainActivity2 extends Activity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main2);

        textView = findViewById(R.id.textView2);

        Intent intent = new Intent();
        String userName = intent.getStringExtra("userInput");
        textView.setText(userName);


    }

    public void changeScreen (View view) {

        Intent intent = new Intent(MainActivity2.this, HelloMapActivity.class);
        startActivity(intent);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("GPS access permission");
        alert.setMessage("This application want to access your device location");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //save
                Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_LONG).show();

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Not Accepted",Toast.LENGTH_LONG).show();

            }
        });

        alert.show();

    }
}