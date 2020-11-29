package com.example.popularmovies.repository.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import com.example.popularmovies.Constants;
import com.example.popularmovies.repository.network.MoviesDataSource;

import java.util.List;

@Database(entities = {Result.class, Favourite.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "popularmovies";
    private static AppDatabase sInstance;
    public DBDataSourceFactory dbDataSourceFactory;
    public DBDataSourceFactory dbFavDataSourceFactory;
    private LiveData<PagedList<Result>> moviesPagedDB;
    private LiveData<PagedList<Result>> moviesPagedDBTopRated;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        sInstance.initAllMovies(sInstance.moviesDao());
        return sInstance;
    }

    public abstract MoviesDao moviesDao();

    public abstract FavouritesDao favouritesDao();


    public LiveData<PagedList<Result>> getMovies() {
        return moviesPagedDB;
    }

    public LiveData<PagedList<Result>> getTopRatedDB() {
        return moviesPagedDBTopRated;
    }

    private void initAllMovies(MoviesDao moviesDao) {

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(MoviesDataSource.PAGE_SIZE)
                .setInitialLoadSizeHint(MoviesDataSource.PAGE_SIZE)
                .build();

        String currentSortingMode = Constants.MOST_POPULAR;
        dbDataSourceFactory = new DBDataSourceFactory(moviesDao, currentSortingMode, null);
        moviesPagedDB = new LivePagedListBuilder<>(dbDataSourceFactory, config).build();

    }

    public LiveData<PagedList<Result>> getMoviesFavPagedDB(String sortingMode, List<Integer> list) {

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(MoviesDataSource.PAGE_SIZE)
                .setInitialLoadSizeHint(MoviesDataSource.PAGE_SIZE)
                .build();

        dbFavDataSourceFactory = new DBDataSourceFactory(moviesDao(), sortingMode, list);
        return new LivePagedListBuilder<>(dbFavDataSourceFactory, config).build();

    }

    public void initTopRated(MoviesDao moviesDao) {

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(MoviesDataSource.PAGE_SIZE)
                .setInitialLoadSizeHint(MoviesDataSource.PAGE_SIZE)
                .build();

        DBDataSourceFactory dbDataSourceFactoryTopRated = new DBDataSourceFactory(moviesDao, Constants.TOP_RATED, null);
        moviesPagedDBTopRated = new LivePagedListBuilder<>(dbDataSourceFactoryTopRated, config).build();

    }


    @SuppressWarnings("unused")
    @Dao
    public interface MoviesDao {

        @Query("SELECT * FROM movies order by popularity Desc")
        List<Result> getMostPopularMovies();

        @Query("SELECT * FROM movies where voteCount> 200 order by voteAverage Desc")
        List<Result> getTopRatedMovies();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertMovie(Result result);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertMoviesList(List<Result> results);

        @Update(onConflict = OnConflictStrategy.REPLACE)
        void updateTask(Result result);

        @Delete
        void deleteTask(Result result);

        @Query("SELECT * FROM movies WHERE id = :id")
        LiveData<Result> loadMovieById(int id);

        @Query("SELECT * FROM movies WHERE id IN (:ids)")
        List<Result> loadMoviesByIds(List<Integer> ids);
    }
}
