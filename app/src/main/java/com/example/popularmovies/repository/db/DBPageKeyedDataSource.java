package com.example.popularmovies.repository.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.example.popularmovies.Constants;

import java.util.List;
import java.util.Objects;

public class DBPageKeyedDataSource extends PageKeyedDataSource<Integer, Result> {

    private static final String TAG = DBPageKeyedDataSource.class.getSimpleName();
    private final AppDatabase.MoviesDao moviesDao;
    private final List<Integer> mList;
    private final String mSortingType;

    public DBPageKeyedDataSource(AppDatabase.MoviesDao dao, String sortingType, List<Integer> list) {
        mSortingType = sortingType;
        moviesDao = dao;
        mList = list;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Result> callback) {
        Log.i(TAG, "Loading Initial Rang, Count " + params.requestedLoadSize);
        List<Result> movies = null;


        if (Objects.equals(mSortingType, Constants.TOP_RATED)) {
            movies = moviesDao.getTopRatedMovies();
        } else if (Objects.equals(mSortingType, Constants.MOST_POPULAR)) {
            movies = moviesDao.getMostPopularMovies();
        } else if ((Objects.equals(mSortingType, Constants.FAVOURITES))) {

            movies = moviesDao.loadMoviesByIds(mList);
        }

        if (movies != null && movies.size() != 0) {
            callback.onResult(movies, 0, 1);
        }
    }


    @Override
    public void loadBefore
            (@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Result> callback) {

    }

    @Override
    public void loadAfter
            (@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Result> callback) {

    }

}


