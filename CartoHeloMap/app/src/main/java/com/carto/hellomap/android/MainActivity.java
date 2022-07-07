package com.carto.hellomap.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String userName;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        userName = "";
    }

    public void changeActivity(View view) {

        userName = editText.getText().toString();

        Intent intent = new Intent(MainActivity.this, MainActivity2.class);

        intent.putExtra("userInput",userName);

        startActivity(intent);




    }
}
