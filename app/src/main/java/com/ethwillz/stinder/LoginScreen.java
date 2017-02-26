package com.ethwillz.stinder; /**
 * Created by maxtalley on 2/25/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginScreen extends AppCompatActivity {
    private EditText email, password, major, name, username;
    private Button signIn, signUp, resetPass;
    private FirebaseAuth authent;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    String inputEmail, inputPass, inputMajor, inputName, inputUsername;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent i = new Intent(this, MainScreen.class);
            startActivity(i);
        }


        authent = FirebaseAuth.getInstance();
        signIn = (Button) findViewById(R.id.sign_in_button);
        signUp = (Button) findViewById(R.id.sign_up_button);
        resetPass = (Button) findViewById(R.id.btn_reset_pass);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        major = (EditText) findViewById(R.id.major);
        name = (EditText) findViewById(R.id.name);
        mDatabase = FirebaseDatabase.getInstance().getReference();


//        resetPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(com.ethwillz.stinder.LoginScreen.this, ResetPasswordActivity.class));
//            }
//        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail = email.getText().toString().trim();
                inputPass = password.getText().toString().trim();
                inputMajor = major.getText().toString().trim();
                inputName = name.getText().toString().trim();
                inputUsername = username.getText().toString().trim();

                if (TextUtils.isEmpty(inputEmail)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(inputPass)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(inputUsername)) {
                    Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(inputMajor)) {
                    Toast.makeText(getApplicationContext(), "Please enter your major", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(inputName)) {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be more than 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                authent.createUserWithEmailAndPassword(inputEmail, inputPass)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(LoginScreen.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginScreen.this, "Authentication Failed" + task.getException(), Toast.LENGTH_LONG).show();
                                } else {
                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    mDatabase.child("users").child(user.getUid()).child("username").setValue(inputUsername);
                                    mDatabase.child("users").child(user.getUid()).child("name").setValue(inputName);
                                    mDatabase.child("users").child(user.getUid()).child("email").setValue(inputEmail);
                                    mDatabase.child("users").child("uid").child("major").setValue(inputMajor);
                                    startActivity(new Intent(LoginScreen.this, MainScreen.class));
                                    finish();
                                }
                            }
                        });



            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
}
