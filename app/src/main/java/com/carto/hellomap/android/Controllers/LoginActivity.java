package com.carto.hellomap.android.Controllers;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carto.hellomap.android.Helpers.EncryptedSP;
import com.carto.hellomap.android.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends Activity {

    private final static int RC_SIGN_IN = 123;

    FirebaseAuth fAuth;
    Button buttonLogin, loginWithGoogle;
    EditText email, password;
    CheckBox rememberMe;
    TextView signUp;
    SharedPreferences sharedPreferences;
    EncryptedSP encryptedSP;


    @SuppressLint( "ResourceType" )
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login );

        buttonLogin = findViewById(R.id.button);
        email = findViewById( R.id.emailLogin );
        password = findViewById( R.id.passwordLogin );
        signUp = findViewById( R.id.signUp );
        rememberMe = findViewById( R.id.checkBoxGiris );
        loginWithGoogle = findViewById( R.id.googleLogin );

        fAuth = FirebaseAuth.getInstance();

        //make the password invisible
        password.setTransformationMethod( new PasswordTransformationMethod());

        sharedPreferences = this.getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        encryptedSP = new EncryptedSP( this, "RememberMe" );

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // String savedEmail = sharedPreferences.getString( "email", "" );
        // String savedPassword = sharedPreferences.getString( "password", "" );
        String savedEmail = encryptedSP.getString( "email" );
        String savedPassword = encryptedSP.getString( "password" );

        rememberMe.setChecked( sharedPreferences.contains( "checked" ) && sharedPreferences.getBoolean( "checked", false ) );

        if( rememberMe.isChecked()){
            email.setText( savedEmail );
            password.setText( savedPassword );
        }
        else{
            email.setText( "" );
            password.setText( "" );
        }


        if( fAuth.getCurrentUser() != null){
            startActivity( new Intent( LoginActivity.this, DashboardActivity.class) );
            finish();
        }

        signUp.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick( View view ) {
                startActivity( new Intent( LoginActivity.this, RegisterActivity.class) );
                finish();
           }
        });

        buttonLogin.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString().trim();
                String password_entered = password.getText().toString().trim();


                if ( TextUtils.isEmpty( mail ) ) {
                    email.setError( "Email adresinizi giriniz." );
                    return;
                }

                if ( TextUtils.isEmpty( password_entered ) ) {
                    password.setError( "Sifre giriniz." );
                    return;
                }


                fAuth.signInWithEmailAndPassword( mail, password_entered ).addOnCompleteListener( new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete( @NonNull Task< AuthResult > task ) {
                        if( task.isSuccessful()){
                            Toast.makeText(getBaseContext(), "Giris Yapildi.", Toast.LENGTH_SHORT).show();

                            //editor.putString( "email", mail );
                            //editor.putString( "password", password_entered );
                            encryptedSP.putString( "email", mail );
                            encryptedSP.putString( "password", password_entered );
                            editor.putString( "userId", fAuth.getCurrentUser().getUid() );
                            editor.apply();

                            Intent loggedIn = new Intent( LoginActivity.this, DashboardActivity.class);
                            startActivity(loggedIn);
                        }
                        else{
                            Toast.makeText( LoginActivity.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );
            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean( "checked", buttonView.isChecked() );
                editor.apply();
            }

        });

        loginWithGoogle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent(LoginActivity.this, LoginWithGoogle.class) );
                finish();

            }
        } );

    }


}