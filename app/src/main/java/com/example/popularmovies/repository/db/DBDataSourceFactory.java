package com.example.popularmovies.repository.db;

import androidx.paging.DataSource;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DBDataSourceFactory extends DataSource.Factory {

    private final AppDatabase.MoviesDao mDao;
    private final List<Integer> mList;
    private final String mCurrentSortingMode;
    public DBPageKeyedDataSource moviesPageKeyedDataSource;

    public DBDataSourceFactory(AppDatabase.MoviesDao dao, String currentSortingMode, List<Integer> List) {
        mDao = dao;
        mCurrentSortingMode = currentSortingMode;
        mList = List;
        moviesPageKeyedDataSource = new DBPageKeyedDataSource(mDao, mCurrentSortingMode, mList);
    }

    @NotNull
    @Override
    public DataSource create() {
        moviesPageKeyedDataSource = new DBPageKeyedDataSource(mDao, mCurrentSortingMode, mList);

        return moviesPageKeyedDataSource;

    }
}
