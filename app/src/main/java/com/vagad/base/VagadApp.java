package com.vagad.base;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.vagad.storage.SharedPreferenceUtil;


/**
 * Created by ubuntu on 15/9/16.
 */
public class VagadApp extends Application {

    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferenceUtil.init(getApplicationContext());
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

}
