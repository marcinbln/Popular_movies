package com.example.popularmovies.repository.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class Favourite {

    @PrimaryKey
    public Integer id;

    public void setId(Integer id) {
        this.id = id;
    }


}

