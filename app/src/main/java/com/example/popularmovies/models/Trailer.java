package com.example.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class Trailer {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("type")
    @Expose
    private String type;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

}
