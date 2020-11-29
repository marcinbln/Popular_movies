package com.example.popularmovies.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.Result;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TopRatedViewModel extends AndroidViewModel {

    private final MoviesRepository repository;

    public TopRatedViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
    }

    public LiveData<PagedList<Result>> getTopRatedMovies(String sortType) {
        return repository.getTopRatedRepo(sortType);
    }

    public MutableLiveData<List<Result>> getMoviesMutable() {
        return repository.getResultsMutable();
    }

}
