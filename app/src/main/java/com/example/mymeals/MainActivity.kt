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
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.navigation.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.json.JSONObject
import kotlinx.coroutines.launch
import coil.compose.AsyncImage


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
                                    //navController.navigate("ipScreen/$imageRes")
                                    navController.navigate("search")
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

                        composable("search"){
                            MealSearchScreen()
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












data class MealResponse(
    val meals: List<Meal>
)

data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)



suspend fun searchMeals(query: String): List<Meal> {
    return withContext(Dispatchers.IO) {

        try {
            val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$query")
            val connection = url.openConnection() as HttpURLConnection

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = reader.readText()

            val json = JSONObject(result)
            val mealsArray = json.getJSONArray("meals")

            val meals = mutableListOf<Meal>()

            for (i in 0 until mealsArray.length()) {
                val obj = mealsArray.getJSONObject(i)

                meals.add(
                    Meal(
                        idMeal = obj.getString("idMeal"),
                        strMeal = obj.getString("strMeal"),
                        strMealThumb = obj.getString("strMealThumb")
                    )
                )
            }

            meals

        } catch (e: Exception) {
            emptyList()
        }
    }
}


@Composable
fun MealSearchScreen() {

    var query by remember { mutableStateOf("") }
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Meal Search", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

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
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = meal.strMealThumb,
                        contentDescription = meal.strMeal,
                        modifier = Modifier
                            .size(80.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = meal.strMeal,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}







