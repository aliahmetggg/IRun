package com.carto.hellomap.android.Controllers;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.carto.hellomap.android.R.string.default_web_client_id;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carto.hellomap.android.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginWithGoogle extends Activity {


    Button login_button;
    EditText google_email;
    FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private static final int REQ_ONE_TAP = 2;
    SharedPreferences sharedPreferences;


    @Override
    public void onStart() {
        super.onStart();



    }

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login_google );

        login_button = (Button) findViewById( R.id.google_button );
        google_email = (EditText ) findViewById( R.id.email_login_google );
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );

        oneTapClient = Identity.getSignInClient(this);
        BeginSignInRequest signInRequest =
                BeginSignInRequest.builder().setPasswordRequestOptions( BeginSignInRequest.PasswordRequestOptions.builder().setSupported( true ).build() ).setGoogleIdTokenRequestOptions( BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported( true )
                        .setServerClientId( getString( default_web_client_id ) )
                        .setFilterByAuthorizedAccounts( false ).build() )

                .setAutoSelectEnabled( true ).build();

        oneTapClient.beginSignIn( signInRequest )
                .addOnSuccessListener(this, new OnSuccessListener< BeginSignInResult >() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);

                        } catch ( IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getLocalizedMessage() );
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REQ_ONE_TAP ) {
            try {
                SignInCredential credential =
                        oneTapClient.getSignInCredentialFromIntent( data );
                String idToken = credential.getGoogleIdToken();

                if ( idToken != null ) {
                    firebaseAuthWithGoogle( idToken );

                    Log.d( TAG, "Got ID token." );
                }

            } catch ( ApiException e ) {
                Log.e(TAG, e.getLocalizedMessage());

            }
        }
    }




    private void firebaseAuthWithGoogle( String idToken ) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString( "userId",mAuth.getCurrentUser().getUid());
                            editor.apply();

                            startActivity( new Intent( LoginWithGoogle.this, DashboardActivity.class) );

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }
}
