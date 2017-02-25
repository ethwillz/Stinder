package com.ethwillz.stinder;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

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
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.firebase.crash.FirebaseCrash.log;

public class Chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //String accountName = ChatSetup.getAccount();

        // Initialize the scope using the client ID you got from the Console.
        final String scope = "audience:server:client_id:"
                + "717236120005-8u7jg89a5ao2lrj3lhqe7f6l7bqi1e6t.apps.googleusercontent.com";
        String idToken = null;
        try {
            //idToken = GoogleAuthUtil.getToken(this.getBaseContext(), accountName, scope);
        } catch (Exception e) {
            log("exception while getting idToken: " + e);
        }


    }
}
