package com.carto.hellomap.android.Controllers;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.carto.hellomap.android.R;

public class OpeningPageActivity extends Activity {
    private Handler handler;
    private TextView txt_app_name;
    private Animation textViewAnimation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_openingpage );

        txt_app_name=findViewById(R.id.txt_app_name);
        textViewAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.textview_animation);

        handler = new Handler();
        View decordView = getWindow().getDecorView();
        decordView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN


        );
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( OpeningPageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable,5000);

        txt_app_name.setAnimation(textViewAnimation);



    }
}