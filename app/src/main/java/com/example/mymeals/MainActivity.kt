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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymeals.db.MealRepository
import com.example.mymeals.screens.CategoriesViewModel
import com.example.mymeals.screens.CategoryViewModel
import com.example.mymeals.screens.CategoryViewModelFactory
import com.example.mymeals.screens.FavouriteMealsViewModel
import com.example.mymeals.screens.FavouriteMealsViewModelFactory
import com.example.mymeals.screens.ScreenCategories
import com.example.mymeals.screens.ScreenCategory
import com.example.mymeals.screens.SearchViewModel
import com.example.mymeals.screens.ViewMealViewModel
import com.example.mymeals.screens.ViewMealViewModelFactory

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

        val repository = MealRepository(db.mealDao())

        //rendering
        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                MyMealsTheme {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "favourites") {

                        composable("favourites") {

                            val viewModel: FavouriteMealsViewModel = viewModel(
                                factory = FavouriteMealsViewModelFactory(repository)
                            )

                            ScreenFavouriteMeals(
                                viewModel = viewModel,
                                onMealClick = { meal ->
                                    val mealJson = URLEncoder.encode(meal.toString(), "UTF-8")
                                    navController.navigate("view/$mealJson")
                                },
                                onAddClick = {
                                    navController.navigate("search")
                                },
                                onMoreClick = {}
                            )
                        }

                        composable(
                            route = "view/{meal}",
                            arguments = listOf(navArgument("meal") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val mealJson = backStackEntry.arguments?.getString("meal")
                            val meal = mealJson?.let { JSONObject(URLDecoder.decode(it, "UTF-8")) }
                            if (meal != null) {
                                val viewModel: ViewMealViewModel = viewModel(
                                    factory = ViewMealViewModelFactory(repository = repository)
                                )

                                ScreenViewMeal(
                                    viewModel = viewModel,
                                    meal = meal,
                                    onDbAltered = { /* opcjonalnie */ }
                                )
                            }
                        }

                        composable("search") {
                            val viewModel: SearchViewModel = viewModel()

                            ScreenSearchMeals(
                                viewModel = viewModel,
                                onMealClick = { meal ->
                                    val mealJson = URLEncoder.encode(meal.toString(), "UTF-8")
                                    navController.navigate("view/$mealJson")
                                },
                                onCategoriesClick = {
                                    navController.navigate("categories")
                                }
                            )
                        }

                        //categories
                        composable("categories") {
                            ScreenCategories(
                                onCategoryClick = { category ->
                                    navController.navigate("category/${category.optString("strCategory")}")
                                }
                            )
                        }

                        composable(
                            route = "category/{categoryName}",
                            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                            val viewModel: CategoryViewModel = viewModel(
                                factory = CategoryViewModelFactory(categoryName)
                            )

                            ScreenCategory(
                                categoryName = categoryName,
                                viewModel = viewModel,
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



















