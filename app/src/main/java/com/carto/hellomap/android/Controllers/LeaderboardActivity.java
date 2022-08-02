package com.carto.hellomap.android.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carto.hellomap.android.Adapters.UserAdapter;
import com.carto.hellomap.android.Models.User;
import com.carto.hellomap.android.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LeaderboardActivity extends Activity{
    FirebaseFirestore fStore;
    CollectionReference doc;
    UserAdapter adapter;
    RecyclerView recyclerView;

    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.leaderboard_activity );

        fStore = FirebaseFirestore.getInstance();
        doc = fStore.collection( "Users" );

        setUpRecyclerView();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    public void setUpRecyclerView(){
        Query query = doc.orderBy( "TotalDistance", Query.Direction.DESCENDING );
        FirestoreRecyclerOptions< User > options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery( query, User.class ).build();

        adapter = new UserAdapter( options );
        recyclerView = findViewById( R.id.recyclerLeaderboard );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setAdapter( adapter );
    }
    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
}
