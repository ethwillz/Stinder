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

public class ResetPassword extends AppCompatActivity{
    private FirebaseAuth authent;
    String inputEmail;
    private Button resetPass;
    private EditText email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        authent = FirebaseAuth.getInstance();
        resetPass = (Button) findViewById(R.id.btn_reset_pass);
        email = (EditText) findViewById(R.id.email);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail = email.getText().toString().trim();

                authent.sendPasswordResetEmail(inputEmail);
                Toast.makeText(ResetPassword.this, "Email Sent", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ResetPassword.this, LoginScreen.class));
            }
        });
    }

}
