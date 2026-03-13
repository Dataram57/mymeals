package com.example.mymeals.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)

suspend fun searchMeals(query: String): List<Meal> {
    return withContext(Dispatchers.IO) {

        try {
            val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$query")
            val connection = url.openConnection() as HttpURLConnection

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = reader.readText()

            val json = JSONObject(result)
            val mealsArray = json.getJSONArray("meals")

            val meals = mutableListOf<Meal>()

            for (i in 0 until mealsArray.length()) {
                val obj = mealsArray.getJSONObject(i)

                meals.add(
                    Meal(
                        idMeal = obj.getString("idMeal"),
                        strMeal = obj.getString("strMeal"),
                        strMealThumb = obj.getString("strMealThumb")
                    )
                )
            }

            meals

        } catch (e: Exception) {
            emptyList()
        }
    }
}

suspend fun fetchMealById(id: String): Meal? {
    return withContext(Dispatchers.IO) {

        try {
            val url = URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=$id")
            val connection = url.openConnection() as HttpURLConnection

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val jsonText = reader.readText()

            val json = JSONObject(jsonText)
            val mealsArray = json.getJSONArray("meals")
            val obj = mealsArray.getJSONObject(0)

            Meal(
                idMeal = obj.getString("idMeal"),
                strMeal = obj.getString("strMeal"),
                strMealThumb = obj.getString("strMealThumb")
            )

        } catch (e: Exception) {
            null
        }
    }
}

suspend fun fetchIp(): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://dataram57.com/ip/")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            val reader = BufferedReader(
                InputStreamReader(connection.inputStream)
            )

            reader.readText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
