package com.example.popularmovies.repository.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToFavourites(Favourite favourites);

    @Query("SELECT id FROM favourites where id = :tid")
    LiveData<Boolean> isFavourited(Integer tid);

    @Query("SELECT id FROM favourites")
    LiveData<List<Integer>> getAllFavourites();

    @Delete
    void deleteFromFavourites(Favourite favourite);


}