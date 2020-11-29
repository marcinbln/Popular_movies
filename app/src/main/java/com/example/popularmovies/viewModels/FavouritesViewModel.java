package com.example.popularmovies.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.Result;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class FavouritesViewModel extends AndroidViewModel {


    private final MoviesRepository repository;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
    }

    public LiveData<PagedList<Result>> getDbFavPaged(String sortingMode, List<Integer> list) {
        return repository.getDbFavPaged(sortingMode, list);
    }

    public LiveData<List<Integer>> getDbFavIntegers() {
        return repository.getFavsIntegersRepo();
    }


}
