package com.example.popularmovies.viewModels;

import com.example.popularmovies.models.Trailer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersJson {

    @SerializedName("results")
    @Expose
    private final List<Trailer> results = null;

    public List<Trailer> getResults() {
        return results;
    }

}

