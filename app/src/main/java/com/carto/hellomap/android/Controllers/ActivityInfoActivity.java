package com.carto.hellomap.android.Controllers;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.carto.hellomap.android.R;

public class ActivityInfoActivity extends Activity{

    TextView duration, distance;

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.info_page );

        duration = findViewById( R.id.dur );
        distance = findViewById( R.id.dis );

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String dis = "";
        String dur = "";

        if( bundle != null){
            dur = bundle.getString("duration");
            dis = bundle.getString( "distance" );
        }

        duration.setText( dur );
        distance.setText( dis );
    }
}
