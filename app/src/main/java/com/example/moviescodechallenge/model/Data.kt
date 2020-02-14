package com.example.moviescodechallenge.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_table")
data class Data(
    val genre: String,
    @PrimaryKey
    val id: Int,
    val poster: String,
    val title: String,
    val year: String)