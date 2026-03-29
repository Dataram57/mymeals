package com.example.mymeals.screens
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymeals.api.fetchMealById
import com.example.mymeals.db.MealDao
import com.example.mymeals.db.MealRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

//================================================================
//ViewModel - Factory

class FavouriteMealsViewModelFactory(
    private val repository: MealRepository
)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteMealsViewModel(repository) as T
    }
}

//================================================================
//ViewModel

class FavouriteMealsViewModel(
    private val repository: MealRepository
)
    : ViewModel() {

    //All meals
    var meals by mutableStateOf<List<JSONObject>>(emptyList())
        private set

    //================================================================
    //Filters

    //Filtered meals
    var filteredMeals by mutableStateOf<List<JSONObject>>(emptyList())
        private set

    //Filters
    var categories by mutableStateOf<List<String>>(emptyList())
        private set
    var tags by mutableStateOf<List<String>>(emptyList())
        private set
    var ingredients by mutableStateOf<List<String>>(emptyList())
        private set

    //States Filter Selectors
    var selectedCategory by mutableStateOf<String?>(null)
    var selectedTag by mutableStateOf<String?>(null)
    var selectedIngredient by mutableStateOf<String?>(null)

    //================================================================
    //Functions

    fun loadMeals() {
        viewModelScope.launch {
            meals = repository.getFavouriteMeals()
            buildFilterOptions()
            applyFilters()
        }
    }

    private fun buildFilterOptions() {
        categories = meals.mapNotNull { it.optString("strCategory").takeIf { it.isNotBlank() && it != "null" } }
            .distinct()

        tags = meals.flatMap {
            it.optString("strTags", "")
                .split(",")
                .map { tag -> tag.trim() }
                .filter { tag -> tag.isNotBlank() && tag != "null" }
        }.distinct()

        ingredients = meals.flatMap { meal ->
            (1..20).mapNotNull { i ->
                meal.optString("strIngredient$i").takeIf { it.isNotBlank() && it != "null" }
            }
        }.distinct()
    }

    fun applyFilters() {
        filteredMeals = meals.filter { meal ->
            val categoryMatches = selectedCategory?.let { it == meal.optString("strCategory") } ?: true
            val tagMatches = selectedTag?.let { tag ->
                meal.optString("strTags", "").split(",").any { it.trim() == tag }
            } ?: true
            val ingredientMatches = selectedIngredient?.let { ingredient ->
                (1..20).any { i -> meal.optString("strIngredient$i", "").equals(ingredient, true) }
            } ?: true

            categoryMatches && tagMatches && ingredientMatches
        }
    }

    fun setCategoryFilter(category: String?) {
        selectedCategory = category
        applyFilters()
    }

    fun setTagFilter(tag: String?) {
        selectedTag = tag
        applyFilters()
    }

    fun setIngredientFilter(ingredient: String?) {
        selectedIngredient = ingredient
        applyFilters()
    }
}

//================================================================
//Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFavouriteMeals(
    viewModel: FavouriteMealsViewModel,
    onMealClick: (JSONObject) -> Unit,
    onAddClick: () -> Unit,
) {

    val meals = viewModel.filteredMeals

    LaunchedEffect(Unit) {
        //instead of LaunchedEffect(reloadDb)
        viewModel.loadMeals()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourite Meals") },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            FilterRow(viewModel = viewModel)

            LazyColumn {
                items(meals) { meal ->
                    FavouriteMealItemView(
                        meal = meal,
                        onOptionClick = onMealClick
                    )
                }
            }
        }
    }
}

//------------------------------------------------
//GUI - Specific Meal Item

@Composable
fun FavouriteMealItemView(
    meal: JSONObject,
    onOptionClick: (JSONObject) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val tags = meal.optString("strTags", "")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() && it != "null" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = meal.getString("strMealThumb"),
                contentDescription = meal.getString("strMeal"),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.getString("strMeal"),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    var currentRowWidth = 0.dp
                    var row = mutableListOf<String>()
                    val maxWidth = 300.dp
                    tags.forEach { tag ->
                        val tagWidth = 60.dp
                        if (currentRowWidth + tagWidth > maxWidth) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                row.forEach { t -> TagItem(t) }
                            }
                            row = mutableListOf(tag)
                            currentRowWidth = tagWidth
                        } else {
                            row.add(tag)
                            currentRowWidth += tagWidth + 4.dp
                        }
                    }
                    if (row.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEach { t -> TagItem(t) }
                        }
                    }
                }
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Ingredients:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

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

//------------------------------------------------
//GUI - Tag Item

@Composable
fun TagItem(tag: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

//------------------------------------------------
//GUI - Filters

@Composable
fun FilterRow(viewModel: FavouriteMealsViewModel) {
    Row(modifier = Modifier.padding(8.dp)) {
        FilterDialogButton(
            label = "Category",
            options = viewModel.categories,
            selectedOption = viewModel.selectedCategory,
            onOptionSelected = { viewModel.setCategoryFilter(it) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterDialogButton(
            label = "Tag",
            options = viewModel.tags,
            selectedOption = viewModel.selectedTag,
            onOptionSelected = { viewModel.setTagFilter(it) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        FilterDialogButton(
            label = "Ingredient",
            options = viewModel.ingredients,
            selectedOption = viewModel.selectedIngredient,
            onOptionSelected = { viewModel.setIngredientFilter(it) }
        )
    }
}

@Composable
fun FilterDialogButton(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }) {
        Text(selectedOption ?: label)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(label) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    content = {
                        item {
                            Text(
                                text = "All",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(null)
                                        showDialog = false
                                    }
                                    .padding(8.dp)
                            )
                        }

                        items(options) { option ->
                            Text(
                                text = option,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(option)
                                        showDialog = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
