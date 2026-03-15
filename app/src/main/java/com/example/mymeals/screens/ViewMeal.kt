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
import com.example.mymeals.api.fetchMealById
import com.example.mymeals.db.FavouriteMeal
import com.example.mymeals.db.MealDao
import kotlinx.coroutines.launch

@Composable
fun ScreenViewMeal(
    meal: JSONObject? = null,
    mealDao: MealDao,
    onDbAltered: (Boolean) -> Unit
) {
    if (meal == null) {
        Text("No meal data available", modifier = Modifier.padding(24.dp))
        return
    }

    val context = LocalContext.current // ✅ Get context once inside composable

    val ingredients = remember {
        (1..20).mapNotNull { i ->
            val ingredient = meal.optString("strIngredient$i").takeIf { it.isNotBlank() && it != "null" }
            val measure = meal.optString("strMeasure$i").takeIf { it.isNotBlank() }
            if (ingredient != null && measure != null) "$ingredient - $measure" else null
        }
    }


    var isFavourite by remember { mutableStateOf(false) }
    var wasFavourite by remember { mutableStateOf(false) }
    var isDisabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val r = mealDao.getMeal(meal.getString("idMeal"))
        if (r != null) {
            isFavourite = true
            wasFavourite = true
        }
        isDisabled = false
    }
    val scope = rememberCoroutineScope()

    Scaffold(

    ) { padding ->
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
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // ⭐ Star Button
                IconButton(
                    onClick = {
                        if (isDisabled) return@IconButton

                        scope.launch {
                            isDisabled = true

                            if (isFavourite) {
                                mealDao.deleteMeal(meal.getString("idMeal"))
                            } else {
                                mealDao.insertMeal(
                                    FavouriteMeal(meal.getString("idMeal"))
                                )
                            }
                            isFavourite = !isFavourite
                            isDisabled = false

                            //db altered
                            onDbAltered(wasFavourite != isFavourite)
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
                        tint = if (isFavourite) Color(0xFFFFD700) else Color.LightGray
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
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            ingredients.forEach { item ->
                Text(text = "• $item", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = meal.optString("strInstructions"),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            val youtubeUrl = meal.optString("strYoutube")
            if (youtubeUrl.isNotBlank()) {
                Text(
                    text = "Watch on YouTube",
                    color = Color(0xFF1A73E8),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
                        context.startActivity(intent) // ✅ use context from composable
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
                        val intent = Intent(Intent.ACTION_VIEW, sourceUrl.toUri())
                        context.startActivity(intent) // ✅ same here
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}