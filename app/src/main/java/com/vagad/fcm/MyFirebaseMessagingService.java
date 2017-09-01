package com.vagad.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vagad.utils.NotificationUtils;

import org.json.JSONObject;

import java.net.URLDecoder;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleDataMessage(JSONObject json) {
        try {
            String message = URLDecoder.decode(json.getString("message"),"UTF-8");
            String title = URLDecoder.decode(json.getString("title"),"UTF-8");
            if(!json.has("image"))
                new NotificationUtils().generateNotification(getApplicationContext(), title, message, json.getBoolean("from_mobile"));
            else
                new NotificationUtils().showBigNotification(URLDecoder.decode(json.getString("image"),"UTF-8"), title,  getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}