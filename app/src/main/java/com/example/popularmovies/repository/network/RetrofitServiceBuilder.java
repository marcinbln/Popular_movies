package com.example.popularmovies.repository.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceBuilder {
    // Base URL
    private static final String URL = "https://api.themoviedb.org/3/discover/";

    // Create Retrofit Builder
    private static final Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create());

    // Create Retrofit Instance
    private static final Retrofit retrofit = builder.build();

    public static <T> T buildService(Class<T> type) {
        return retrofit.create(type);
    }


}
