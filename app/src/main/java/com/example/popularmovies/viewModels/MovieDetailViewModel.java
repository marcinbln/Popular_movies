package com.example.popularmovies.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.ui.MovieDetailActivity;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.Result;
import com.example.popularmovies.models.Review;
import com.example.popularmovies.repository.db.AppDatabase;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class MovieDetailViewModel extends AndroidViewModel {


    private final LiveData<Result> result;
    private final LiveData<Boolean> isFavourited;
    private final LiveData<List<Review>> reviewList;
    int movieID;

    public MovieDetailViewModel(@NonNull Application application) {
        super(application);

        result = AppDatabase.getInstance(application).moviesDao().loadMovieById(MovieDetailActivity.movieID);
        isFavourited = AppDatabase.getInstance(application).favouritesDao().isFavourited(MovieDetailActivity.movieID);
        reviewList = MoviesRepository.getInstance(application).getReviewList();
    }


    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public LiveData<List<Review>> getReviewList() {
        return reviewList;
    }

    public LiveData<Result> getResult() {
        return result;
    }

    public LiveData<Boolean> isFavourite() {
        return isFavourited;
    }

    public void setId(int id) {
    }

    @Override
    protected void onCleared() {
        MoviesRepository.getInstance(getApplication()).resetReviews();
        super.onCleared();
    }
}
