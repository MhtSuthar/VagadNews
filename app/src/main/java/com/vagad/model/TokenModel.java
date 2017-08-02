package com.vagad.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Admin on 25-Jul-17.
 */
@IgnoreExtraProperties
public class TokenModel {

    public String device_token;
    public String key;

    public TokenModel() {}

    public TokenModel(String token, String key) {
        this.device_token = token;
        this.key = key;
    }

}
