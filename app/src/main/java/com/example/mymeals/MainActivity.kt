package com.example.mymeals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mymeals.ui.theme.MyMealsTheme
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.room.Room
import com.example.mymeals.screens.ScreenFavouriteMeals
import com.example.mymeals.screens.ScreenSearchMeals
import com.example.mymeals.screens.ScreenViewMeal
import com.example.mymeals.db.MealDao
import org.json.JSONObject
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mymeals.db.FavouriteMeal
import java.net.URLDecoder
import java.net.URLEncoder


class MainActivity : ComponentActivity() {

    private lateinit var mealDao: MealDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //db
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "meals_db"
        ).build()
        mealDao = db.mealDao()

        //rendering
        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                MyMealsTheme {

                    val navController = rememberNavController()
                    var reloadDb by remember { mutableStateOf(true) }

                    NavHost(navController = navController, startDestination = "favourites") {

                        composable("favourites") {
                            //reload db
                            ScreenFavouriteMeals(
                                mealDao = mealDao,
                                onMealClick = { meal ->
                                    val mealJson = URLEncoder.encode(meal.toString(), "UTF-8")
                                    navController.navigate("view/$mealJson")
                                },
                                onAddClick = {
                                    navController.navigate("search")
                                },
                                onMoreClick = {},
                                reloadDb = reloadDb
                            )

                            // Reset reloadDb after passing it once
                            if (reloadDb) reloadDb = false
                        }

                        composable(
                            route = "view/{meal}",
                            arguments = listOf(navArgument("meal") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val mealJson = backStackEntry.arguments?.getString("meal")
                            val meal = mealJson?.let { JSONObject(URLDecoder.decode(it, "UTF-8")) }
                            if (meal != null) {
                                ScreenViewMeal(
                                    mealDao = mealDao,
                                    meal = meal,
                                    onDbAltered = { reloadDb = it })
                            }
                        }

                        composable("search") {
                            ScreenSearchMeals(
                                onMealClick = { meal ->
                                    val mealJson = URLEncoder.encode(meal.toString(), "UTF-8")
                                    navController.navigate("view/$mealJson")
                                }

                            )
                        }

                    }
                }
            }
        }
    }
}



















