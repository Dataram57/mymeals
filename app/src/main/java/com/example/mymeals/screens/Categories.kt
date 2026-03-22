package com.example.mymeals.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymeals.api.fetchCategories
import com.example.mymeals.db.MealRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


class CategoriesViewModel : ViewModel() {

    var categories by mutableStateOf<List<JSONObject>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadCategories() {
        viewModelScope.launch {
            isLoading = true
            categories = fetchCategories() // bezpośrednie wywołanie API
            isLoading = false
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCategories(
    viewModel: CategoriesViewModel = viewModel(),
    onCategoryClick: (JSONObject) -> Unit
) {
    val categories = viewModel.categories
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Categories") }) }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: JSONObject, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        AsyncImage(
            model = category.optString("strCategoryThumb"),
            contentDescription = category.optString("strCategory"),
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = category.optString("strCategory"),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = category.optString("strCategoryDescription"),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}