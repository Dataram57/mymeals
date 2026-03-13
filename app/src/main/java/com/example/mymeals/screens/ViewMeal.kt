package com.example.mymeals.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.mymeals.api.fetchIp
import org.json.JSONObject
import androidx.core.net.toUri

@Composable
fun ScreenViewMeal(
    meal: JSONObject? = null,
) {
    if (meal == null) {
        Text("No meal data available", modifier = Modifier.padding(24.dp))
        return
    }

    val context = LocalContext.current // ✅ Get context once inside composable

    val ingredients = remember {
        (1..20).mapNotNull { i ->
            val ingredient = meal.optString("strIngredient$i").takeIf { it.isNotBlank() }
            val measure = meal.optString("strMeasure$i").takeIf { it.isNotBlank() }
            if (ingredient != null && measure != null) "$ingredient - $measure" else null
        }
    }

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
            coil.compose.AsyncImage(
                model = meal.optString("strMealThumb"),
                contentDescription = meal.optString("strMeal"),
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

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