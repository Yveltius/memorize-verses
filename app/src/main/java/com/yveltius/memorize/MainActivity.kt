package com.yveltius.memorize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yveltius.memorize.ui.screens.AddVerseScreen
import com.yveltius.memorize.ui.screens.VerseListScreen
import com.yveltius.memorize.ui.theme.MemorizeVersesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = VerseRelatedScreens.VerseList.name) {
                composable(route = VerseRelatedScreens.VerseList.name) {
                    VerseListScreen(onAddVerse = { navController.navigate(route = VerseRelatedScreens.AddVerse.name) })
                }
                composable(route = VerseRelatedScreens.AddVerse.name) {
                    AddVerseScreen(onBackPress = { navController.navigateUp() })
                }
            }
        }
    }
}

enum class VerseRelatedScreens {
    VerseList,
    AddVerse
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MemorizeVersesTheme {
        Greeting("Android")
    }
}