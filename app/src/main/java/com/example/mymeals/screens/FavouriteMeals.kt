package com.example.mymeals.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mymeals.api.Meal
import com.example.mymeals.api.fetchMealById
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao

@Composable
fun ScreenFavouriteMeals(
    mealDao: MealDao,
    onOptionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    LaunchedEffect(Unit) {

        var ids = getSavedMealIds(mealDao)

        if(ids.size == 0){
            mealDao.insertMeal(FavouriteMeal("52771"))
            ids = getSavedMealIds(mealDao)
        }


        val loadedMeals = mutableListOf<Meal>()

        for (id in ids) {
            val meal = fetchMealById(id)
            if (meal != null) loadedMeals.add(meal)
        }

        meals = loadedMeals
    }

    LazyColumn {

        items(meals) { meal ->

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                coil.compose.AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = meal.strMeal,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(meal.strMeal)
            }
        }
    }
}



suspend fun getSavedMealIds(mealDao: MealDao): List<String> {
    return mealDao.getAllMeals().map { it.idMeal }
}

@Composable
fun MealItemView(meal: MealItem, onOptionClick: (Int) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Left red importance column
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(64.dp)
                    .background(
                        if (meal.isImportant) Color.Red else Color.Transparent
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Image
            Image(
                painter = painterResource(meal.imageRes),
                contentDescription = meal.title,
                modifier = Modifier
                    .size(64.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            Text(
                text = meal.title,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Collapsible section
        if (expanded) {
            Column(modifier = Modifier.padding(start = 80.dp)) {

                Text(
                    "Show IP",
                    modifier = Modifier.clickable {
                        onOptionClick(meal.imageRes)
                    }
                )
            }
        }
    }
}




data class MealItem(
    val id: String,
    val title: String,
    val imageRes: Int,
    val isImportant: Boolean
)

