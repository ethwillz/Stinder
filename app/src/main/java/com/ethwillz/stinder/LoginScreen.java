package com.ethwillz.stinder;

/**
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginScreen extends AppCompatActivity {
    private EditText email, password;
    private Button signIn, signUp, resetPass;
    private FirebaseAuth authent;
    String inputEmail, inputPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

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

                authent.signInWithEmailAndPassword(inputEmail, inputPass)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(LoginScreen.this, "" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginScreen.this, "Authentication Failed" /*+ task.getException()*/, Toast.LENGTH_LONG).show();
                                } else {
                                    startActivity(new Intent(LoginScreen.this, MainScreen.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
