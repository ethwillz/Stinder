package com.ethwillz.stinder;

/**
 * Created by maxtalley on 2/25/17.
 */

import android.*;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import static com.google.firebase.crash.FirebaseCrash.log;

public class LoginScreen extends AppCompatActivity {
    private EditText email, password;
    private Button signIn, signUp, resetPass;
    private FirebaseAuth authent;
    String inputEmail, inputPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)

//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Intent i = new Intent(this, MainScreen.class);
//            startActivity(i);
//        }
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Intent i = new Intent(this, MainScreen.class);
//            startActivity(i);
//        }

        authent = FirebaseAuth.getInstance();
        signIn = (Button) findViewById(R.id.sign_in_button);
        signUp = (Button) findViewById(R.id.sign_up_button);
        resetPass = (Button) findViewById(R.id.btn_reset_pass);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegistrationScreen.class));
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, ResetPassword.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail = email.getText().toString().trim();
                inputPass = password.getText().toString().trim();

                //Checks if email field is empty, prompts user to enter if empty
                if (TextUtils.isEmpty(inputEmail)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checks if password field is empty, prompts user to enter if empty
                if (TextUtils.isEmpty(inputPass)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checks with Firebase authentication handler if credentials are invalid, shows authentication failed if unsuccessful
                authent.signInWithEmailAndPassword(inputEmail, inputPass)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginScreen.this, "Authentication Failed" /*+ task.getException()*/, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    startActivity(new Intent(LoginScreen.this, MainScreen.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
