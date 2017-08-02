package com.vagad.fcm;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vagad.model.TokenModel;
import com.vagad.storage.SharedPreferenceUtil;
import com.vagad.utils.Constants;

import static com.vagad.utils.AppUtils.isOnline;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private DatabaseReference mDatabase;
    private String refreshedToken = "";

    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferenceUtil.putValue(Constants.FIREBASE_USERS_TOKEN, refreshedToken);
        SharedPreferenceUtil.save();

        Log.e(TAG, "Refreshed token: " + refreshedToken);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        getAndCheckToken();
    }

    protected void getAndCheckToken() {
        if(isOnline(this)){
            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_TOKEN);
            mDatabase.addValueEventListener(valueTokenEventListener);
        }
    }

    ValueEventListener valueTokenEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() == 0){
                mDatabase.removeEventListener(this);
                addTokenToFirebase();
                return;
            }
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                TokenModel changedPost = messageSnapshot.getValue(TokenModel.class);
                if(!refreshedToken.equals(changedPost.device_token)){
                    mDatabase.removeEventListener(this);
                    addTokenToFirebase();
                    break;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            System.out.println("The read failed: " + databaseError.getCode());
        }
    };

    private void addTokenToFirebase() {
        if(!TextUtils.isEmpty(refreshedToken)) {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_TOKEN);
            String key = mDatabase.push().getKey();
            TokenModel tokenModel = new TokenModel(refreshedToken, key);
            mDatabase.child(key).setValue(tokenModel);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getKey() + "   " + dataSnapshot.getRef() + "" + dataSnapshot.getChildren() + "   " + dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

}