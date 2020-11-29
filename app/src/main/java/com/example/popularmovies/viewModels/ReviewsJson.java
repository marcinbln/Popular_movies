package com.example.popularmovies.viewModels;


import com.example.popularmovies.models.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


@SuppressWarnings({"WeakerAccess", "unused", "CanBeFinal"})
public class ReviewsJson {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Review> results = null;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    @SerializedName("total_results")

    public Integer getId() {
        return id;
    }

    public List<Review> getResults() {
        return results;
    }


}

