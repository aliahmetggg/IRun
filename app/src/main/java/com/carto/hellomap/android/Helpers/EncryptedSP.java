package com.carto.hellomap.android.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptedSP {
    SharedPreferences sharedPreferences;

    public EncryptedSP( Context context, String value) {
        try {
            MasterKey masterKey = new MasterKey.Builder( context ).setKeyScheme( MasterKey.KeyScheme.AES256_GCM ).build();
            sharedPreferences = EncryptedSharedPreferences.create(
                                context, value, masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
        }
        catch( Exception e){
            e.printStackTrace();
        }
    }

    public String getString(String name){
        return sharedPreferences.getString(name, null);
    }

    public void putString( String name, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( name, value );
        editor.apply();
    }
}
