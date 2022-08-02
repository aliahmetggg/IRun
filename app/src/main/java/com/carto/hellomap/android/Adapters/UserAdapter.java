package com.carto.hellomap.android.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carto.hellomap.android.Models.User;
import com.carto.hellomap.android.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class UserAdapter extends FirestoreRecyclerAdapter< User, UserAdapter.UserHolder > {

    int count = 0;
    public UserAdapter( @NonNull FirestoreRecyclerOptions options ) {
        super( options );
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.leaderboard_row, parent, false );
        return new UserHolder( view );
    }

    @Override
    protected void onBindViewHolder( @NonNull UserHolder holder,
                                     int position, @NonNull User model ) {

        holder.userName.setText( model.getName() + " " + model.getSurname());
        holder.userScore.setText(  model.getTotalDistance() + " km" );
        holder.rank.setText( String.valueOf(position + 1) );

    }


    public class UserHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView userScore;
        TextView rank;

        public UserHolder( @NonNull View itemView ) {
            super( itemView );
            count++;

            userName = itemView.findViewById( R.id.user_name );
            userScore = itemView.findViewById( R.id.user_score );
            rank = itemView.findViewById( R.id.number );

            if( count > 10){
                itemView.setVisibility( View.GONE );
            }
        }
    }
}
