package com.foxden.fitnessapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.ui.ActivityJournalScreen
import com.foxden.fitnessapp.ui.ActivityRecordingScreen
import com.foxden.fitnessapp.ui.AddManualActivityScreen
import com.foxden.fitnessapp.ui.DBTestScreen
import com.foxden.fitnessapp.ui.HomeScreen
import com.foxden.fitnessapp.ui.SettingsScreen
import com.foxden.fitnessapp.ui.NutritionTrackingScreen
import com.foxden.fitnessapp.ui.ProfileSettings
import com.foxden.fitnessapp.ui.DisplaySettings
import com.foxden.fitnessapp.ui.theme.FitnessAppTheme
import com.foxden.fitnessapp.utils.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

object Routes {
    const val HOME_SCREEN = "HomeScreen"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ACTIVITY_JOURNAL_SCREEN = "ActivityJournalScreen"
    const val ACTIVITY_RECORDING_SCREEN = "ActivityRecordingScreen"
    const val NUTRITION_TRACKING_SCREEN = "NutritionTrackingScreen"

    const val ADD_ACTIVITY_FORM_SCREEN = "AddActivityFormScreen"

    const val PROFILE_SETTINGS_SCREEN = "ProfileSettingsScreen"
    const val DISPLAY_SETTINGS_SCREEN = "DisplaySettingsScreen"

    const val DBTEST_SCREEN = "DBTestScreen"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationViewModel: LocationViewModel by viewModels()
        val db = DBHelper(this)

        setContent {
            val navController = rememberNavController()

            FitnessAppTheme {
                NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {
                    composable(
                        Routes.HOME_SCREEN,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        HomeScreen(navController, application)
                    }
                    composable(
                        Routes.ACTIVITY_JOURNAL_SCREEN,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        ActivityJournalScreen(navController)
                    }

                    composable(
                        Routes.ADD_ACTIVITY_FORM_SCREEN,
                        enterTransition = {EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        AddManualActivityScreen(navController)
                    }

                    composable(
                        Routes.ACTIVITY_RECORDING_SCREEN,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        ActivityRecordingScreen(navController, locationViewModel)
                    }
                    composable(
                        Routes.NUTRITION_TRACKING_SCREEN,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        NutritionTrackingScreen(navController)
                    }
                    composable(
                        Routes.SETTINGS_SCREEN,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        SettingsScreen(navController)
                    }

                    composable(Routes.PROFILE_SETTINGS_SCREEN) {
                        ProfileSettings(navigation = navController)
                    }
                    composable(Routes.DISPLAY_SETTINGS_SCREEN) {
                        DisplaySettings(navigation = navController)
                    }

                    composable(Routes.DBTEST_SCREEN) {
                        DBTestScreen(navController, db)
                    }
                }
            }
        }
    }
}