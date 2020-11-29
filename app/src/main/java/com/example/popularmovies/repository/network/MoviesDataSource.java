package com.example.popularmovies.repository.network;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.popularmovies.Constants;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.Result;
import com.example.popularmovies.models.MovieDiscover;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesDataSource extends PageKeyedDataSource<Integer, Result> {

    public static final int PAGE_SIZE = 20;
    private static final int FIRST_PAGE = 1;

    private String mSortBy;
    private Integer mVote_countGTE;


    public MoviesDataSource(String sortingType) {

        if (Objects.equals(sortingType, Constants.TOP_RATED)) {
            mSortBy = "vote_average.desc";
            mVote_countGTE = 200;
        } else if (Objects.equals(sortingType, Constants.MOST_POPULAR)) {
            mSortBy = "popularity.desc";
            mVote_countGTE = null;
        }
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Result> callback) {

        RetrofitClient service = RetrofitServiceBuilder.buildService(RetrofitClient.class);
        Call<MovieDiscover> call = service.getMovies(FIRST_PAGE, mSortBy, mVote_countGTE);
        call.enqueue(new Callback<MovieDiscover>() {
                         @Override
                         public void onResponse(@NotNull Call<MovieDiscover> call, @NotNull Response<MovieDiscover> response) {

                             MovieDiscover listView = response.body();
                             //Save to database
                             if (listView != null) {
                                 List<Result> responseItems = response.body().getResults();

                                 MoviesRepository.instance.setResultsMutable(responseItems);

                                 callback.onResult(responseItems, 1, FIRST_PAGE + 1);

                             }
                         }

                         @Override
                         public void onFailure(@NotNull Call<MovieDiscover> call, @NotNull Throwable t) {
                             MoviesRepository.instance.setResultsMutable(null);

                             callback.onResult(new ArrayList<>(), 1, 2);
                         }
                     }
        );
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Result> callback) {

        RetrofitClient service = RetrofitServiceBuilder.buildService(RetrofitClient.class);
        Call<MovieDiscover> call = service.getMovies(params.key, mSortBy, mVote_countGTE);
        call.enqueue(new Callback<MovieDiscover>() {
            @Override
            public void onResponse(@NotNull Call<MovieDiscover> call, @NotNull Response<MovieDiscover> response) {

                MovieDiscover listView = response.body();

                if (listView != null) {
                    List<Result> responseItems = listView.getResults();
                    MoviesRepository.instance.setResultsMutable(responseItems);
                    callback.onResult(responseItems, params.key + 1);
                }
            }

            @Override
            public void onFailure(@NotNull Call<MovieDiscover> call, @NotNull Throwable t) {
                callback.onResult(new ArrayList<>(), 1);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Result> callback) {
    }
}
