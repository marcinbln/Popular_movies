package com.example.popularmovies.repository.network;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.example.popularmovies.repository.db.Result;


public class MoviesDataSourceFactory extends DataSource.Factory<Integer, Result> {

    private final String currentSortingMode;
    public MoviesDataSource moviesPageKeyedDataSource;

    public MoviesDataSourceFactory(String sortType) {
        currentSortingMode = sortType;
        //default query parameters = movie discover
        moviesPageKeyedDataSource = new MoviesDataSource(sortType);

    }

    @NonNull
    @Override
    public DataSource<Integer, Result> create() {
        moviesPageKeyedDataSource = new MoviesDataSource(currentSortingMode);
        return moviesPageKeyedDataSource;
    }

}

