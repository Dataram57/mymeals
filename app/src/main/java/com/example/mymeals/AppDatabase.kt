package com.example.mymeals

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao

@Database(entities = [FavouriteMeal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
}
