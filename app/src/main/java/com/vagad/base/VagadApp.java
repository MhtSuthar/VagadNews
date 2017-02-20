package com.vagad.base;

import android.app.Application;

import com.vagad.storage.SharedPreferenceUtil;


/**
 * Created by ubuntu on 15/9/16.
 */
public class VagadApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferenceUtil.init(getApplicationContext());
    }

}
