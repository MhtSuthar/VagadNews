package com.vagad.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.vagad.utils.DateUtils;

import java.io.Serializable;
import java.util.Calendar;

// [START blog_user_class]
@IgnoreExtraProperties
public class MusicModel implements Serializable{

    public String name;
    public String size;
    public String file_path;
    public String description;
    public String key;

    public MusicModel() {}


    public MusicModel(String name, String size, String file_path, String description, String key) {
        this.name = name;
        this.size = size;
        this.file_path = file_path;
        this.description = description;
        this.key = key;
    }
}

