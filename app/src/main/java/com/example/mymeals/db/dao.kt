package com.example.mymeals.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    @Insert
    suspend fun insertMeal(meal: FavouriteMeal)

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<FavouriteMeal>

    @Query("DELETE FROM meals")
    suspend fun clearMeals()
}