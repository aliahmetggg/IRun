package com.carto.hellomap.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carto.hellomap.android.Controllers.ActivityInfoActivity;
import com.carto.hellomap.android.Models.Activity;
import com.carto.hellomap.android.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    ArrayList< Activity > activities;

    public CustomAdapter( Context context, ArrayList< Activity > activities){
        this.context = context;
        this.activities = activities;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        LayoutInflater layoutInflater = LayoutInflater.from( context);
        View view = layoutInflater.inflate(R.layout.pastactivities_row, parent, false);
        return new MyViewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull CustomAdapter.MyViewHolder holder,
                                  int position ) {

        holder.duration.setText( String.valueOf( activities.get(position).getDuration()) );
        holder.distance.setText( String.valueOf( activities.get(position).getDistance() + " km") );
        holder.date.setText( String.valueOf( activities.get(position).getDate() ) );

    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView distance, duration, date;
        ImageButton infoButton;

        public MyViewHolder( @NonNull View itemView ) {
            super( itemView );

            distance = itemView.findViewById( R.id.distance);
            duration = itemView.findViewById( R.id.duration);
            date = itemView.findViewById( R.id.date);
            infoButton = itemView.findViewById( R.id.info_button );

            infoButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    Intent intent = new Intent( context, ActivityInfoActivity.class);
                    intent.putExtra( "distance", distance.getText().toString() );
                    intent.putExtra( "duration", duration.getText().toString() );
                    context.startActivity( intent);
                }
            } );

        }
    }
}
