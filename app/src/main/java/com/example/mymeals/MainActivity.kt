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
            setContent {
                MyMealsTheme {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "favourites") {

                        composable("favourites") {
                            ScreenFavouriteMeals(
                                mealDao = mealDao,
                                onOptionClick = { meal ->
                                    //navController.navigate("search")
                                    //navController.navigate("view/" + meal.toString())

                                    //pass complex object
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("meal", meal.toString())

                                    //navigate to view
                                    navController.navigate("view")
                                }
                            )
                        }

                        composable("view") {
                            //get complex argument
                            val mealString = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<String>("meal")

                            val meal = mealString?.let { JSONObject(it) }
                            //pass
                            if(meal != null)
                                ScreenViewMeal(
                                    meal = meal
                                )
                        }

                        composable("search"){
                            ScreenSearchMeals(
                                mealDao = mealDao
                            )
                        }

                    }
                }
            }
        }
    }
}



















