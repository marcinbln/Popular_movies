package com.example.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class Review {

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("url")

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
