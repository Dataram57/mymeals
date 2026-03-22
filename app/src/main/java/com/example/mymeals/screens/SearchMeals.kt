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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymeals.api.searchMeals
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymeals.api.searchMeals
import kotlinx.coroutines.launch
import org.json.JSONObject

class SearchViewModel : ViewModel() {

    var query by mutableStateOf("")
        private set

    var meals by mutableStateOf<List<JSONObject>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    fun searchMeals() {
        viewModelScope.launch {
            isLoading = true
            try {
                meals = searchMeals(query)
            } finally {
                isLoading = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSearchMeals(
    viewModel: SearchViewModel,
    onMealClick: (JSONObject) -> Unit
) {
    val query = viewModel.query
    val meals = viewModel.meals

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meal Search") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    label = { Text("Search meal") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    keyboardActions = KeyboardActions(
                        onSearch = { viewModel.searchMeals() }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    )
                )

                Button(onClick = { viewModel.searchMeals() }) {
                    Text("Search")
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(meals) { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMealClick(meal) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = meal.getString("strMealThumb"),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(meal.getString("strMeal"))
                    }
                }
            }
        }
    }
}