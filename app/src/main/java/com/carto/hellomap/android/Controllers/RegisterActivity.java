package com.carto.hellomap.android.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.carto.hellomap.android.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {

    String[] genderSelection ={ "Kadın", "Erkek"};
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String gender;
    SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_register );

        EditText editName = findViewById(R.id.nameRegister );
        EditText editSurname = findViewById(R.id.surnameRegister );
        EditText editEmail = findViewById(R.id.emailRegister );
        EditText editPassword = findViewById(R.id.passwordRegister );
        TextView login = findViewById( R.id.loginPage );
        TextView signUpWithGoogle = findViewById( R.id.signUpWithGoogle );
        Button buttonSignUp = findViewById(R.id.signUpButton );
        Spinner spinnerGender = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderSelection );

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editPassword.setTransformationMethod( new PasswordTransformationMethod() );

        if( fAuth.getCurrentUser() != null){
            startActivity( new Intent( RegisterActivity.this, DashboardActivity.class) );
            finish();
        }


        login.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent_login = new Intent( RegisterActivity.this, LoginActivity.class);
                startActivity( intent_login );
            }
        } );

        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = spinnerGender.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = "";
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String name = editName.getText().toString().trim();
                String surname = editSurname.getText().toString().trim();

                if( TextUtils.isEmpty(mail)){
                    editEmail.setError( "Email adresinizi giriniz." );
                    return;
                }

                  if( TextUtils.isEmpty(password)) {
                      editPassword.setError( "Sifre giriniz." );
                      return;
                  }
                  if( TextUtils.isEmpty(name)){
                      editEmail.setError( "Adınızı giriniz." );
                      return;
                  }

                  if( TextUtils.isEmpty(surname)) {
                      editPassword.setError( "Soyadınızı giriniz." );
                      return;
                  }

                  fAuth.createUserWithEmailAndPassword( mail, password ).addOnCompleteListener( new OnCompleteListener< AuthResult >() {
                      @Override
                      public void onComplete( @NonNull Task< AuthResult > task ) {
                        if( task.isSuccessful()){
                              Toast.makeText(getBaseContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();

                              userId = fAuth.getCurrentUser().getUid();

                              DocumentReference document = fStore.collection( "Users" ).document(userId);

                              Map<String, Object> userKey = new HashMap<>();
                              userKey.put("Name", name);
                              userKey.put("Surname", surname);
                              userKey.put("Email", mail);
                              userKey.put("Gender", gender);
                              userKey.put( "TotalDistance", 0);
                              document.set(userKey).addOnSuccessListener( new OnSuccessListener< Void >() {
                                  @Override
                                  public void onSuccess( Void unused ) {
                                      Log.d("TAG", userId + " is saved." );
                                  }
                              } );

                            editor.putString( "userId", fAuth.getCurrentUser().getUid() );
                            editor.apply();

                              Intent signup_success = new Intent( RegisterActivity.this, DashboardActivity.class);
                              startActivity(signup_success);
                          }
                          else{
                              Toast.makeText( RegisterActivity.this, "Error!" + task.getException().getMessage(),Toast.LENGTH_SHORT ).show();
                          }
                      }
                  } );

              }
         });

        signUpWithGoogle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                startActivity( new Intent( RegisterActivity.this, SignUpWithGoogle.class) );
            }
        } );
    }
}