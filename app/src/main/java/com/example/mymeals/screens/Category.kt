package com.example.mymeals.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.example.mymeals.api.fetchCategory
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.mymeals.api.fetchMealById


class CategoryViewModel(
    private val categoryName: String
) : ViewModel() {

    var meals by mutableStateOf<List<JSONObject>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadMeals()
    }

    fun loadMeals() {
        viewModelScope.launch {
            isLoading = true
            meals = fetchCategory(categoryName)
            isLoading = false
        }
    }
}

class CategoryViewModelFactory(
    private val categoryName: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(categoryName) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCategory(
    categoryName: String,
    viewModel: CategoryViewModel,
    onMealClick: (JSONObject) -> Unit
) {
    val meals = viewModel.meals
    val isLoading = viewModel.isLoading
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text(categoryName) }) }
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(meals) { meal ->
                    CategoryMealItem(
                        meal = meal,
                        onClick = {
                            // 🔥 Pobranie pełnych detali przed przekazaniem dalej
                            scope.launch {
                                val fullMeal = fetchMealById(meal.optString("idMeal"))
                                if (fullMeal != null) {
                                    onMealClick(fullMeal
                                        .getJSONArray("meals")
                                        .getJSONObject(0)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryMealItem(meal: JSONObject, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = meal.optString("strMealThumb"),
            contentDescription = meal.optString("strMeal"),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = meal.optString("strMeal"),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}