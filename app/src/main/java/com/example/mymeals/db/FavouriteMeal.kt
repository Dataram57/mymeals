package com.example.mymeals.db;
import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class FavouriteMeal(
    @PrimaryKey
    val idMeal: String,
)