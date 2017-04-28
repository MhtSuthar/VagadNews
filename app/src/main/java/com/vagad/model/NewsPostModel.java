package com.vagad.model;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class NewsPostModel {

    public String nameReporter;
    public String newsTitle;
    public String newsDesc;

    public NewsPostModel() {}

    public NewsPostModel(String nameReporter, String newsDesc, String newsTitle) {
        this.nameReporter = nameReporter;
        this.newsTitle = newsTitle;
        this.newsDesc = newsDesc;
    }

}

