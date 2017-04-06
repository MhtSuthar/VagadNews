package com.vagad.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class NewsPostModel {

    public String username;
    public String email;

    public NewsPostModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public NewsPostModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
// [END blog_user_class]
