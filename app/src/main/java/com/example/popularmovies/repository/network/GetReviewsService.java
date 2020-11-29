package com.example.popularmovies.repository.network;

import com.example.popularmovies.viewModels.ReviewsJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static com.example.popularmovies.Constants.THEMOVIEDB_API_KEY;

public interface GetReviewsService {

    @GET("https://api.themoviedb.org/3/movie/{movieId}/reviews?api_key=" + THEMOVIEDB_API_KEY)
    Call<ReviewsJson> getReviews(@Path("movieId") Integer movieId);

}
