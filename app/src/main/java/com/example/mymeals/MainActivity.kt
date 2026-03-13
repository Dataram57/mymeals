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


class MainActivity : ComponentActivity() {

    private lateinit var mealDao: MealDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "meals_db"
        ).build()

        mealDao = db.mealDao()

        enableEdgeToEdge()
        setContent {
            setContent {
                MyMealsTheme {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "list") {

                        composable("list") {
                            ScreenFavouriteMeals(
                                mealDao = mealDao,
                                onOptionClick = { imageRes ->
                                    //navController.navigate("ipScreen/$imageRes")
                                    navController.navigate("search")
                                }
                            )
                        }

                        composable(
                            route = "ipScreen/{imageRes}",
                            arguments = listOf(navArgument("imageRes") { type = NavType.IntType })
                        ) { backStackEntry ->

                            val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

                            ScreenViewMeal(imageRes)
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



















