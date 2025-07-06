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
import androidx.navigation.toRoute
import com.yveltius.memorize.ui.screens.AddVerseScreen
import com.yveltius.memorize.ui.screens.VerseListScreen
import com.yveltius.memorize.ui.theme.MemorizeVersesTheme
import com.yveltius.versememorization.entity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = VerseList) {
                composable<VerseList> {
                    VerseListScreen(
                        onAddVerse = { navController.navigate(route = AddVerse) },
                        onEditVerse = { verse -> navController.navigate(route = EditVerse(verseUUIDString = verse.uuid.toString())) }
                    )
                }
                composable<AddVerse> {
                    AddVerseScreen(onBackPress = { navController.navigateUp() })
                }

                composable<EditVerse> { backStackEntry ->
                    val editVerse = backStackEntry.toRoute<EditVerse>()
                    AddVerseScreen(
                        onBackPress = { navController.navigateUp() },
                        verseUUID = UUID.fromString(editVerse.verseUUIDString)
                    )
                }
            }
        }
    }
}

@Serializable
object VerseList

@Serializable
object AddVerse

@Serializable
data class EditVerse(
    val verseUUIDString: String
)

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