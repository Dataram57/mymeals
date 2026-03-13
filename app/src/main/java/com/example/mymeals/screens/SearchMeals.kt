package com.example.mymeals.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymeals.api.Meal
import com.example.mymeals.api.searchMeals
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ScreenSearchMeals(
    mealDao: MealDao
) {

    var query by remember { mutableStateOf("") }
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Meal Search", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search meal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    meals = searchMeals(query)
                }
            }
        ) {
            Text("Search")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {

            items(meals) { meal ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = meal.strMealThumb,
                        contentDescription = meal.strMeal,
                        modifier = Modifier
                            .size(80.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = meal.strMeal,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    val scope = rememberCoroutineScope()

                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            mealDao.insertMeal(FavouriteMeal(meal.idMeal))
                        }
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}




