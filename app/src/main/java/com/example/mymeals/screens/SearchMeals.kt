package com.example.mymeals.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.mymeals.api.searchMeals
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSearchMeals(
    onMealClick : (JSONObject) -> Unit
) {

    var query by remember { mutableStateOf("") }
    var meals by remember { mutableStateOf<List<JSONObject>>(emptyList()) }

    val scope = rememberCoroutineScope()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Meal Search") }
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)  // your inner padding
        ) {

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
                            .clickable {
                                onMealClick(meal)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = meal.getString("strMealThumb"),
                            contentDescription = meal.getString("strMeal"),
                            modifier = Modifier
                                .size(80.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = meal.getString("strMeal"),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        val scope = rememberCoroutineScope()

                    }
                }
            }
        }
    }
}




