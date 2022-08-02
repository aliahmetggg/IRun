package com.carto.hellomap.android.Controllers;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.MotionEffect;

import com.carto.hellomap.android.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class SignUpWithGoogle extends Activity {

    private SignInClient oneTapClient;
    private static final int REQ_ONE_TAP = 2;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    SharedPreferences sharedPreferences;


    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.sign_up_google );

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );

        oneTapClient = Identity.getSignInClient( getApplicationContext() );
        BeginSignInRequest signUpRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions( BeginSignInRequest.GoogleIdTokenRequestOptions.
                builder().setSupported( true ).setServerClientId( getString( R.string.default_web_client_id ) ).
                setFilterByAuthorizedAccounts( false ).build() ).build();

        oneTapClient.beginSignIn( signUpRequest ).addOnSuccessListener( this, new OnSuccessListener< BeginSignInResult >() {
            @Override
            public void onSuccess( BeginSignInResult result ) {
                try {
                    startIntentSenderForResult( result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0 );
                } catch ( IntentSender.SendIntentException e ) {
                    Log.e( TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage() );
                }
            }
        } ).addOnFailureListener( this, new OnFailureListener() {
            @Override
            public void onFailure( @NonNull Exception e ) {
                Log.d( TAG, e.getLocalizedMessage() );
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REQ_ONE_TAP ) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent( data );

                String idToken = credential.getGoogleIdToken();
                String email = credential.getId();
                String name = credential.getGivenName();
                String surname = credential.getFamilyName();

                if ( idToken != null ) {
                    firebaseAuthWithGoogle( idToken, email, name, surname );
                    Log.d( TAG, "Got ID token." );
                }
            } catch ( ApiException e ) {
                Log.e( MotionEffect.TAG, e.getLocalizedMessage());
            }
        }
    }

    public void firebaseAuthWithGoogle( String idToken, String email, String name, String surname ) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "suc", Toast.LENGTH_SHORT).show();

                            String userId = fAuth.getCurrentUser().getUid();

                            DocumentReference document = fStore.collection( "Users" ).document(userId);

                            Map<String, Object> userKey = new HashMap<>();
                            userKey.put("Name", name);
                            userKey.put("Surname", surname);

                            userKey.put("Email", email);
                            userKey.put( "TotalDistance", 0);
                            document.set(userKey).addOnSuccessListener( new OnSuccessListener< Void >() {
                                @Override
                                public void onSuccess( Void unused ) {
                                    Log.d("TAG", userId + " is saved." );
                                }
                            } );

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString( "userId",userId);
                            editor.apply();

                            finish();
                            startActivity( new Intent( SignUpWithGoogle.this, DashboardActivity.class) );

                        } else {
                            Toast.makeText(getBaseContext(), "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
