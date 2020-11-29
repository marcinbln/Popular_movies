package com.example.popularmovies.repository.network;


import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.popularmovies.repository.db.Result;

public class MoviesNetwork {

    final private LiveData<PagedList<Result>> moviesPagedNetwork;


    public MoviesNetwork(MoviesDataSourceFactory dataSourceFactory, PagedList.BoundaryCallback<Result> boundaryCallback) {
        PagedList.Config pagedListConfig = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                .setInitialLoadSizeHint(MoviesDataSource.PAGE_SIZE).setPageSize(MoviesDataSource.PAGE_SIZE).build();

        moviesPagedNetwork = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig).setBoundaryCallback(boundaryCallback).build();

    }

    public LiveData<PagedList<Result>> getPagedMovies() {
        return moviesPagedNetwork;
    }


}
