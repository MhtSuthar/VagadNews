package com.vagad.fcm;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vagad.model.TokenModel;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 31-Jul-17.
 */

public class FcmUtils {

    private static final String TAG = "FcmUtils";
    public static final String FCM_SERVER_KEY = "AAAA7Ql0jjA:APA91bFNTqPrKECoQagl7YD_J6esJR06yf9SRFP5ayuMEulRDAo5QYV37Hz10PBe-RaG8GaX4yLVUP-wZG9jdtR81iv6SRO6i66EQgMnUNCHCWg6pWnEUgNeftFGz_PIB6cO6VSOxtpw";

    private static void sendMultipleDeviceNotification(final List<String> token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");

                    JSONArray regId = new JSONArray();
                    JSONObject data = new JSONObject();


                    data.put("from_mobile", true);
                    data.put("title", URLEncoder.encode(SharedPreferenceUtil.getString(Constants.LOCALE_NEWS_TITLE_ADD, ""), "UTF-8"));
                    data.put("message", URLEncoder.encode(SharedPreferenceUtil.getString(Constants.LOCALE_NEWS_DESC_ADD, ""), "UTF-8"));
                    for (int i = 0; i < token.size(); i++) {
                        regId.put(token.get(i));
                    }

                    JSONObject message = new JSONObject();
                    //message.put("to", token);
                    message.put("registration_ids", regId);
                    message.put("priority", "high");
                    message.put("data", data);

                    JSONObject notification = new JSONObject();
                    notification.put("title", "Vagad News");
                    notification.put("text", SharedPreferenceUtil.getString(Constants.LOCALE_NEWS_TITLE_ADD, ""));

                    message.put("notification", notification);

                    Log.e(TAG, "run: "+message.toString());

                    OutputStream os = conn.getOutputStream();
                    os.write(message.toString().getBytes("UTF-8"));
                    os.close();

                    // read the response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.e(TAG, "res  "+result.toString());
                    in.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void sendSingleDeviceNotification(final String token, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");


                    JSONObject data = new JSONObject();
                    data.put("message", URLEncoder.encode(message, "UTF-8"));

                    JSONObject message = new JSONObject();
                    message.put("to", token);
                    message.put("priority", "high");
                    message.put("data", data);

                    JSONObject notification = new JSONObject();
                    notification.put("title", "Vagad News");
                    notification.put("text", message);

                    message.put("notification", notification);

                    OutputStream os = conn.getOutputStream();
                    os.write(message.toString().getBytes("UTF-8"));
                    os.close();

                    // read the response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.e("response", result.toString());
                    in.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static List<String> getAllDeviceToken() {
        final List<String> mDeviceToken = new ArrayList<>();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_TOKEN);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    TokenModel changedPost = messageSnapshot.getValue(TokenModel.class);
                    Log.e(TAG, "onDataChange: "+changedPost.device_token);
                    if(!SharedPreferenceUtil.getString(Constants.FIREBASE_USERS_TOKEN, "").equals(changedPost.device_token))
                        mDeviceToken.add(changedPost.device_token);
                }
                mDatabase.removeEventListener(this);
                sendMultipleDeviceNotification(mDeviceToken);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mDeviceToken;
    }
}
