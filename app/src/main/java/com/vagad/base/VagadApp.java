package com.vagad.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

}
