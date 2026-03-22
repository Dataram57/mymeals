package com.example.mymeals.db

import com.example.mymeals.api.fetchMealById
import com.example.mymeals.api.searchMeals
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import org.json.JSONObject

class MealRepository(
    private val mealDao: MealDao
) {

    // 🔍 SEARCH (API only)
    suspend fun searchMeals(query: String): List<JSONObject> {
        return searchMeals(query)
    }

    // ❤️ FAVOURITES (DB only)
    suspend fun getFavouriteIds(): List<String> {
        return mealDao.getAllMeals().map { it.idMeal }
    }

    suspend fun isFavourite(id: String): Boolean {
        return mealDao.getMeal(id) != null
    }

    suspend fun addFavourite(id: String) {
        mealDao.insertMeal(FavouriteMeal(id))
    }

    suspend fun removeFavourite(id: String) {
        mealDao.deleteMeal(id)
    }

    // 🍽️ SINGLE MEAL (API)
    suspend fun getMealById(id: String): JSONObject? {
        val response = fetchMealById(id)
        return response
            ?.getJSONArray("meals")
            ?.getJSONObject(0)
    }

    // 🔥 COMBINED: favourites + API
    suspend fun getFavouriteMeals(): List<JSONObject> {
        val ids = getFavouriteIds()

        val meals = mutableListOf<JSONObject>()

        for (id in ids) {
            val meal = getMealById(id)
            if (meal != null) meals.add(meal)
        }

        return meals
    }
}