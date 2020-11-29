package com.example.popularmovies.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.popularmovies.Constants;
import com.example.popularmovies.models.Review;
import com.example.popularmovies.repository.db.AppDatabase;
import com.example.popularmovies.repository.db.Result;
import com.example.popularmovies.repository.network.GetReviewsService;
import com.example.popularmovies.repository.network.MoviesDataSourceFactory;
import com.example.popularmovies.repository.network.MoviesNetwork;
import com.example.popularmovies.repository.network.RetrofitReviewsClientInstance;
import com.example.popularmovies.viewModels.ReviewsJson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {

    public static MoviesRepository instance;
    public final PagedList.BoundaryCallback<Result> boundaryCallback;
    public final MoviesDataSourceFactory dataSourceFactoryMostPopular;
    private final LiveData<List<Integer>> favsIntegersRepo;
    private final MutableLiveData<List<Result>> resultsMutable = new MutableLiveData<>();
    private final MediatorLiveData liveDataMergerTopRated;
    private final MediatorLiveData liveDataMergerMostPopular;
    private final MutableLiveData<List<Review>> reviewList;
    public AppDatabase mDb;
    public MoviesDataSourceFactory moviesDataSourceFactoryTopRated;
    private MoviesNetwork networkMostPopular;

    private MoviesRepository(Context context) {
        reviewList = new MediatorLiveData<>();
        liveDataMergerMostPopular = new MediatorLiveData<>();
        liveDataMergerTopRated = new MediatorLiveData<>();
        moviesDataSourceFactoryTopRated = new MoviesDataSourceFactory(Constants.TOP_RATED);

        //From network MostPopular
        dataSourceFactoryMostPopular = new MoviesDataSourceFactory(Constants.MOST_POPULAR);
        boundaryCallback = new PagedList.BoundaryCallback<Result>() {

            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();

                liveDataMergerMostPopular.addSource(mDb.getMovies(), value -> {
                            liveDataMergerMostPopular.setValue(value);
                            liveDataMergerMostPopular.removeSource(mDb.getMovies());
                        }
                );
            }

            @Override
            public void onItemAtFrontLoaded(@NonNull Result itemAtFront) {
                super.onItemAtFrontLoaded(itemAtFront);
            }

            @Override
            public void onItemAtEndLoaded(@NonNull Result itemAtEnd) {
                super.onItemAtEndLoaded(itemAtEnd);
            }
        };

        networkMostPopular = new MoviesNetwork(dataSourceFactoryMostPopular, boundaryCallback);

        mDb = AppDatabase.getInstance(context.getApplicationContext());
        favsIntegersRepo = mDb.favouritesDao().getAllFavourites();

        liveDataMergerMostPopular.addSource(networkMostPopular.getPagedMovies(), liveDataMergerMostPopular::setValue

        );
    }

    public static MoviesRepository getInstance(Context context) {

        if (instance == null) {
            instance = new MoviesRepository(context);
        }
        return instance;
    }

    public MutableLiveData<List<Review>> getReviewList() {

        return reviewList;
    }

    public MutableLiveData<List<Result>> getResultsMutable() {
        return resultsMutable;
    }

    public void setResultsMutable(List<Result> result) {
        resultsMutable.postValue(result);
    }

    public MediatorLiveData getMovies() {
        return liveDataMergerMostPopular;
    }


    public LiveData<PagedList<Result>> getDbFavPaged(String sortingMode, List<Integer> list) {
        return mDb.getMoviesFavPagedDB(sortingMode, list);
    }

    public LiveData<List<Integer>> getFavsIntegersRepo() {
        return favsIntegersRepo;
    }


    public LiveData<PagedList<Result>> getTopRatedRepo(String sortMode) {

        if (moviesDataSourceFactoryTopRated == null) {
            moviesDataSourceFactoryTopRated = new MoviesDataSourceFactory(sortMode);
        }

        PagedList.BoundaryCallback boundaryCallback = new PagedList.BoundaryCallback() {
            @Override
            public void onZeroItemsLoaded() {
                super.onZeroItemsLoaded();
                mDb.initTopRated(mDb.moviesDao());
                liveDataMergerTopRated.addSource(mDb.getTopRatedDB(), value -> {
                            liveDataMergerTopRated.setValue(value);
                            liveDataMergerTopRated.removeSource(mDb.getTopRatedDB());
                        }
                );
            }
        };
        MoviesNetwork networkTopRated = new MoviesNetwork(moviesDataSourceFactoryTopRated, boundaryCallback);

        liveDataMergerTopRated.addSource(networkTopRated.getPagedMovies(), liveDataMergerTopRated::setValue);

        return liveDataMergerTopRated;
    }

    public LiveData<PagedList<Result>> getMostPopularRepo() {
        networkMostPopular = new MoviesNetwork(dataSourceFactoryMostPopular, boundaryCallback);
        return liveDataMergerMostPopular;
    }


    public void resetReviews() {
        reviewList.setValue(new ArrayList<>());
    }

    public void loadReviews(int movieID) {
        /*Create handle for the RetrofitInstance interface*/
        GetReviewsService service = RetrofitReviewsClientInstance.getRetrofitInstance().create(GetReviewsService.class);
        Call<ReviewsJson> call = service.getReviews(movieID);
        call.enqueue(new Callback<ReviewsJson>() {
            @Override
            public void onResponse(Call<ReviewsJson> call, Response<ReviewsJson> response) {
                ReviewsJson reviewsJson = response.body();
                Log.v("reviewsJson ok", reviewsJson + " " + call);
                if (reviewsJson != null) {
                    reviewList.postValue(reviewsJson.getResults());
                }
            }

            @Override
            public void onFailure(Call<ReviewsJson> call, Throwable t) {
                reviewList.postValue(new ArrayList<>());
                Log.v("reviewsJson onFailure", t + " " + call);
            }
        });

    }

}



