package com.example.mymeals.screens

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.mymeals.api.fetchMealById
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import org.json.JSONObject
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFavouriteMeals(
    mealDao: MealDao,
    onMealClick: (JSONObject) -> Unit,
    onAddClick: () -> Unit,
    onMoreClick: () -> Unit,
    reloadDb: Boolean,
    modifier: Modifier = Modifier
) {

    var meals by rememberSaveable { mutableStateOf<List<JSONObject>>(emptyList()) }

    LaunchedEffect(reloadDb) {
        if (reloadDb || meals.isEmpty()) {
            var ids = getSavedMealIds(mealDao)

            val loadedMeals = mutableListOf<JSONObject>()

            meals = emptyList()
            for (id in ids) {
                val meal = fetchMealById(id)
                if (meal != null) loadedMeals.add(meal.getJSONArray("meals").getJSONObject(0))
            }

            meals = loadedMeals
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourite Meals") },
                actions = {

                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new meal."
                        )
                    }

                    /*
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    */

                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            items(meals) { meal ->
                FavouriteMealItemView(
                    meal = meal,
                    onOptionClick = onMealClick
                )
            }
        }

    }
}



suspend fun getSavedMealIds(mealDao: MealDao): List<String> {
    return mealDao.getAllMeals().map { it.idMeal }
}

@Composable
fun FavouriteMealItemView(
    meal: JSONObject,
    onOptionClick: (JSONObject) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded } // toggle expanded
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            //.shadow(2.dp, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Meal image
            AsyncImage(
                model = meal.getString("strMealThumb"),
                contentDescription = meal.getString("strMeal"),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Meal title
                Text(
                    text = meal.getString("strMeal"),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tags example (strTags might be comma separated)
                val tags = meal.optString("strTags", "").split(",").filter { it.isNotBlank() && it != "null" }
                Row {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .padding(end = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Expanded section: ingredients
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(start = 8.dp)) {

                Text(
                    text = "Ingredients:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Example: strIngredient1, strIngredient2, ... up to 20
                for (i in 1..20) {
                    val ingredient = meal.optString("strIngredient$i", "")
                    val measure = meal.optString("strMeasure$i", "")
                    if (ingredient.isNotBlank() && ingredient != "null") {
                        Text(
                            text = "• $ingredient : $measure",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Click to view details",
                    modifier = Modifier.clickable { onOptionClick(meal) },
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}