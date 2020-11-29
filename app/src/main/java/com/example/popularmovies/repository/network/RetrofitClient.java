package com.example.popularmovies.repository.network;


import com.example.popularmovies.viewModels.TrailersJson;
import com.example.popularmovies.models.MovieDiscover;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.example.popularmovies.Constants.THEMOVIEDB_API_KEY;

public interface RetrofitClient {

    //https://api.themoviedb.org/3/discover/movie?api_key=THEMOVIEDB_API_KEY&sort_by=popularity.desc&page=1

    @GET("movie?api_key=" + THEMOVIEDB_API_KEY)
    Call<MovieDiscover> getMovies(@Query("page") Integer page,
                                  @Query("sort_by") String sortBy,
                                  @Query("vote_count.gte") Integer voteCountMin);


    //https://api.themoviedb.org/3/movie/475303/videos?api_key=THEMOVIEDB_API_KEY

    @GET("https://api.themoviedb.org/3/movie/{movieId}/videos?api_key=" + THEMOVIEDB_API_KEY)
    Call<TrailersJson> getTrailers(@Path("movieId") Integer movieId);

}


