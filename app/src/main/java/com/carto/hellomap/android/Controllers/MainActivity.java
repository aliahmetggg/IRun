package com.carto.hellomap.android.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.Activity;

import com.carto.hellomap.android.R;

public class MainActivity extends Activity {

    String userName;
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        editText = findViewById(R.id.editText);
        button = (Button) findViewById( R.id.button );

        userName = "";

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                changeActivity( view );            }
        } );
    }

    public void changeActivity(View view) {

        userName = editText.getText().toString();

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);

        intent.putExtra("userInput",userName);

        startActivity(intent);




    }
}
