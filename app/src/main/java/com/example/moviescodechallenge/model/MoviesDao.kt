package com.example.moviescodechallenge.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies_table")
    suspend fun getMoviesCache() : List<Data>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveCacheData(data: List<Data>)
}