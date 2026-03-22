package com.example.mymeals.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import androidx.core.net.toUri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymeals.db.MealRepository

//import kotlinx.coroutines.launch
class ViewMealViewModel(
    private val repository: MealRepository
) : ViewModel() {

    var meal by mutableStateOf<JSONObject?>(null)
        private set

    var isFavourite by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun loadMeal(id: String) {
        viewModelScope.launch {
            meal = repository.getMealById(id)
            isFavourite = repository.isFavourite(id)
        }
    }

    fun setMealData(newMeal: JSONObject) {
        meal = newMeal
        checkIfFavourite()
    }

    private fun checkIfFavourite() {
        val currentMeal = meal ?: return

        viewModelScope.launch {
            val result = repository.isFavourite(currentMeal.getString("idMeal"))
            isFavourite = result
            isLoading = false
        }
    }

    fun toggleFavourite(onDbAltered: (Boolean) -> Unit) {
        val id = meal?.getString("idMeal") ?: return

        viewModelScope.launch {
            val wasFavourite = isFavourite

            if (isFavourite) {
                repository.removeFavourite(id)
            } else {
                repository.addFavourite(id)
            }

            isFavourite = !isFavourite

            // powiadom UI o zmianie
            onDbAltered(wasFavourite != isFavourite)
        }
    }
}

@Composable
fun ScreenViewMeal(
    viewModel: ViewMealViewModel,
    meal: JSONObject?,
    onDbAltered: (Boolean) -> Unit
) {
    if (meal == null) {
        Text("No meal data available", modifier = Modifier.padding(24.dp))
        return
    }

    val context = LocalContext.current

    val vmMeal = viewModel.meal
    val isFavourite = viewModel.isFavourite
    val isLoading = viewModel.isLoading

    // 🔥 ustaw meal tylko raz
    LaunchedEffect(meal) {
        viewModel.setMealData(meal)
    }

    val ingredients = remember(meal) {
        (1..20).mapNotNull { i ->
            val ingredient = meal.optString("strIngredient$i")
                .takeIf { it.isNotBlank() && it != "null" }

            val measure = meal.optString("strMeasure$i")
                .takeIf { it.isNotBlank() }

            if (ingredient != null && measure != null) {
                "$ingredient - $measure"
            } else null
        }
    }

    Scaffold {
            padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {

                coil.compose.AsyncImage(
                    model = meal.optString("strMealThumb"),
                    contentDescription = meal.optString("strMeal"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                IconButton(
                    onClick = {
                        if (!isLoading) {
                            viewModel.toggleFavourite(onDbAltered)
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Meal",
                        tint = if (isFavourite)
                            Color(0xFFFFD700)
                        else
                            Color.LightGray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = meal.optString("strMeal"),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${meal.optString("strCategory")} | ${meal.optString("strArea")}",
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Text("Ingredients:", fontWeight = FontWeight.Bold)
            ingredients.forEach {
                Text("• $it")
            }

            Spacer(Modifier.height(16.dp))

            Text("Instructions:", fontWeight = FontWeight.Bold)
            Text(meal.optString("strInstructions"))

            Spacer(Modifier.height(16.dp))

            val youtubeUrl = meal.optString("strYoutube")
            if (youtubeUrl.isNotBlank()) {
                Text(
                    text = "Watch on YouTube",
                    color = Color(0xFF1A73E8),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
                        )
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            val sourceUrl = meal.optString("strSource")
            if (sourceUrl.isNotBlank()) {
                Text(
                    text = "Recipe Source",
                    color = Color(0xFF1A73E8),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, sourceUrl.toUri())
                        )
                    }
                )
            }
        }
    }
}


class ViewMealViewModelFactory(
    private val repository: MealRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewMealViewModel(repository) as T
    }
}