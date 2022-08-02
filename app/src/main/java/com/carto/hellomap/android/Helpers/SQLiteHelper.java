package com.carto.hellomap.android.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "MyPastActivity.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_activity";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_DISTANCE = "activity_distance";
    private static final String COLUMN_EPOCH_START = "activity_start_epoch";
    private static final String COLUMN_EPOCH_FINISH = "acitivity_finish_epoch";


    public SQLiteHelper( @Nullable Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context = context;
    }

    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " TEXT, " +
                COLUMN_DISTANCE + " INTEGER, " +
                COLUMN_EPOCH_START + " LONG, " +
                COLUMN_EPOCH_FINISH + " LONG );";

        sqLiteDatabase.execSQL( query );
    }

    @Override
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int i, int i1 ) {
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate( sqLiteDatabase );
    }

    public void addNewActivity( String userId, int distance, long epochStart, long epochFinish){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put( COLUMN_DISTANCE, distance);
        contentValues.put( COLUMN_EPOCH_START, epochStart);
        contentValues.put( COLUMN_EPOCH_FINISH, epochFinish);
        contentValues.put ( COLUMN_USER_ID, userId);


        long result = database.insert( TABLE_NAME, null, contentValues );

        if( result == -1){
            Toast.makeText( context, "Failed", Toast.LENGTH_SHORT ).show();
        }
        else{
            Toast.makeText( context, "Success", Toast.LENGTH_SHORT ).show();
        }

    }

    public Cursor readData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if( db != null){
            cursor = db.rawQuery(query, null);
        }

        return cursor;
    }

}
