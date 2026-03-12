package com.example.mymeals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mymeals.ui.theme.MyMealsTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                MyMealsTheme {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "list") {

                        composable("list") {
                            MealList(
                                meals = sampleMeals,
                                onOptionClick = { imageRes ->
                                    navController.navigate("ipScreen/$imageRes")
                                }
                            )
                        }

                        composable(
                            route = "ipScreen/{imageRes}",
                            arguments = listOf(navArgument("imageRes") { type = NavType.IntType })
                        ) { backStackEntry ->

                            val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0

                            IpScreen(imageRes)
                        }
                    }
                }
            }
        }
    }
}


val sampleMeals = listOf(
    MealItem("Spaghetti", R.drawable.majster1, true),
    MealItem("Burger", R.drawable.majster2, false),
    MealItem("Pizza", R.drawable.majster3, true)
)

@Composable
fun MealList(
    meals: List<MealItem>,
    onOptionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(meals) { meal ->
            MealItemView(meal, onOptionClick)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyMealsTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MealList(
                meals = sampleMeals,
                {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
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
    val title: String,
    val imageRes: Int,
    val isImportant: Boolean
)



@Composable
fun IpScreen(imageRes: Int) {

    var ipText by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        ipText = fetchIp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(ipText)
    }
}



suspend fun fetchIp(): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://dataram57.com/ip/")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"

            val reader = BufferedReader(
                InputStreamReader(connection.inputStream)
            )

            reader.readText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
