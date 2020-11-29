package com.example.popularmovies.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.AppDatabase;
import com.example.popularmovies.repository.db.Result;

import java.util.List;

@SuppressWarnings("ALL")
public class MoviesViewModel extends AndroidViewModel {

    private final String TAG = this.getClass().getSimpleName() + "MyLog";

    private final MoviesRepository repository;

    private final LiveData<List<Integer>> favsIntegers;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
        favsIntegers = AppDatabase.getInstance(application).favouritesDao().getAllFavourites();
    }

    public LiveData<PagedList<Result>> getMoviesPaged() {
        return repository.getMovies();
    }

    public MutableLiveData<List<Result>> getMoviesMutable() {
        return repository.getResultsMutable();
    }

    public void invalidateDataSource() {
        repository.dataSourceFactoryMostPopular.moviesPageKeyedDataSource.invalidate();
        repository.mDb.dbDataSourceFactory.moviesPageKeyedDataSource.invalidate();

    }

    public LiveData<List<Integer>> getFavsIntegers() {
        return favsIntegers;
    }

    public LiveData<PagedList<Result>> getMostPopularRepo(String sortType) {
        return repository.getMostPopularRepo();
    }

}



