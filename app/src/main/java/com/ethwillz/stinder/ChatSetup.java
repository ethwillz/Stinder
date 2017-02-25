package com.ethwillz.stinder;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
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

public class ChatSetup extends FirebaseMessagingService {

    public ChatSetup(){
        super();
    }

    // This snippet takes the simple approach of using the first returned Google account,
    // but you can pick any Google account on the device.
    public String getAccount() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = AccountManager.get(this).
                    getAccountsByType("com.google");
            if (accounts.length == 0) {
                return null;
            }
            return accounts[0].name;
        } else
            return null;
    }

    //Adds device to group
    public String addNotificationKey(
            String senderId, String userEmail, String registrationId, String idToken)
            throws IOException, JSONException {
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");

    }

    //Sends message
    public static void sendMessage(String message) {
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        //String to = aUniqueKey; // the notification key
        AtomicInteger msgId = new AtomicInteger();
        //fm.send(new RemoteMessage.Builder(to).setMessageId(msgId).addData(message).build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        String TAG = "com.ethwillz.strinder";
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
