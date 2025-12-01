package com.yveltius.memorize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.yveltius.memorize.features.addverse.screens.AddVerseScreen
import com.yveltius.memorize.features.settings.screens.SettingsScreen
import com.yveltius.memorize.features.settings.screens.SupportTicketScreen
import com.yveltius.memorize.features.verselist.screens.VerseListScreen
import com.yveltius.memorize.features.choosenextword.screens.ChooseNextWordScreen
import kotlinx.serialization.Serializable
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = VerseList,
            ) {
                composable<VerseList> {
                    VerseListScreen(
                        onAddVerse = { navController.navigate(route = AddVerse) },
                        onEditVerse = { verse -> navController.navigate(route = EditVerse(verse.uuid.toString())) },
                        onGoToChooseNextWord = { verse ->
                            navController.navigate(
                                route = ChooseNextWord(
                                    verseUUIDString = verse.uuid.toString()
                                )
                            )
                        },
                        onGoToSettings = {
                            navController.navigate(route = Settings)
                        }
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

                composable<ChooseNextWord> { backStackEntry ->
                    val chooseNextWord = backStackEntry.toRoute<ChooseNextWord>()
                    ChooseNextWordScreen(
                        onBackPress = { navController.navigateUp() },
                        verseUUIDString = chooseNextWord.verseUUIDString
                    )
                }

                composable<Settings> {
                    SettingsScreen(
                        onBackPress = { navController.navigateUp() },
                        onGoToSupportTicket = { navController.navigate(route = SupportTicket) }
                    )
                }

                composable<SupportTicket> {
                    SupportTicketScreen(onBackPress = { navController.navigateUp() })
                }
            }
        }
    }
}

@Serializable
private object VerseList

@Serializable
private object AddVerse

@Serializable
private data class EditVerse(
    val verseUUIDString: String
)

@Serializable
private data class ChooseNextWord(
    val verseUUIDString: String
)

@Serializable
private object Settings

@Serializable
private object SupportTicket