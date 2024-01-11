package com.foxden.fitnessapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.ActivityJournalScreen
import com.foxden.fitnessapp.ui.ActivityRecordingGPSScreen
import com.foxden.fitnessapp.ui.ActivityRecordingNoGPSScreen
import com.foxden.fitnessapp.ui.ActivityRecordingScreen
import com.foxden.fitnessapp.ui.AddManualActivityScreen
import com.foxden.fitnessapp.ui.DisplaySettings
import com.foxden.fitnessapp.ui.GoalsSettings
import com.foxden.fitnessapp.ui.HomeScreen
import com.foxden.fitnessapp.ui.NutritionAddPresetScreen
import com.foxden.fitnessapp.ui.NutritionLogMealScreen
import com.foxden.fitnessapp.ui.NutritionTrackingScreen
import com.foxden.fitnessapp.ui.ProfileSettings
import com.foxden.fitnessapp.ui.SettingsScreen
import com.foxden.fitnessapp.ui.theme.FitnessAppTheme
import com.foxden.fitnessapp.utils.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

/* List of all navigation routes available in the app */
object Routes {
    const val HOME_SCREEN = "HomeScreen"

    const val ACTIVITY_JOURNAL_SCREEN = "ActivityJournalScreen"
    const val ADD_ACTIVITY_FORM_SCREEN = "AddActivityFormScreen"

    const val ACTIVITY_RECORDING_SCREEN = "ActivityRecordingScreen"
    const val ACTIVITY_RECORDING_NO_GPS_SCREEN = "ActivityRecordingNoGPSScreen"
    const val ACTIVITY_RECORDING_GPS_SCREEN = "ActivityRecordingGPSScreen"

    const val SETTINGS_SCREEN = "SettingsScreen"
    const val PROFILE_SETTINGS_SCREEN = "ProfileSettingsScreen"
    const val DISPLAY_SETTINGS_SCREEN = "DisplaySettingsScreen"
    const val GOALS_SETTINGS_SCREEN = "GoalsSettingsScreen"

    const val NUTRITION_TRACKING_SCREEN = "NutritionTrackingScreen"
    const val NUTRITION_LOG_MEAL_SCREEN = "NutritionLogMealScreen"
    const val NUTRITION_ADD_PRESET_SCREEN = "NutritionAddPresetScreen"
}

/*
MainActivity()

The main class which controls the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationViewModel: LocationViewModel by viewModels()

        // Create instance of database helper, which is passed around via navigation
        val db = DBHelper(this)

        // datastore
        val settingsDataStoreManager = SettingsDataStoreManager(context = this)
        //theme
        val darkModeLiveData = settingsDataStoreManager.checkDarkmode

        darkModeLiveData.observe(this, Observer { isDarkMode ->
            setContent {
                val navController = rememberNavController()
                
                // Create navigation host for each page
                FitnessAppTheme(darkTheme = isDarkMode) {
                    NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {
                        composable(
                            Routes.HOME_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            HomeScreen(navController, db)
                        }
                        composable(
                            Routes.ACTIVITY_JOURNAL_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            ActivityJournalScreen(navController, db)
                        }

                        composable(
                            Routes.ADD_ACTIVITY_FORM_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            AddManualActivityScreen(navController, db)
                        }
                        composable(
                            Routes.ACTIVITY_RECORDING_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            ActivityRecordingScreen(navController, locationViewModel, db)
                        }
                        composable(
                            "${Routes.ACTIVITY_RECORDING_NO_GPS_SCREEN}/{activityTypeId}",
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {navBackStackEntry ->
                            // invalid navigation signaled to component with -1 value
                            val activityTypeId = navBackStackEntry.arguments?.getString("activityTypeId") ?: "-1"
                            ActivityRecordingNoGPSScreen(activityTypeId.toInt(), navController, db)
                        }
                        composable(
                            "${Routes.ACTIVITY_RECORDING_GPS_SCREEN}/{activityTypeId}",
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {navBackStackEntry ->
                            // invalid navigation signaled to component with -1 value
                            val activityTypeId = navBackStackEntry.arguments?.getString("activityTypeId") ?: "-1"
                            ActivityRecordingGPSScreen(activityTypeId.toInt(), navController, db, locationViewModel)
                        }
                        composable(
                            Routes.NUTRITION_TRACKING_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            NutritionTrackingScreen(navController, db)
                        }
                        composable(
                            Routes.NUTRITION_LOG_MEAL_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            NutritionLogMealScreen(navController, db)
                        }
                        composable(
                            Routes.NUTRITION_ADD_PRESET_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            NutritionAddPresetScreen(navController, db)
                        }

                        composable(
                            Routes.SETTINGS_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            SettingsScreen(navController, locationViewModel, db)
                        }

                        composable(
                            Routes.PROFILE_SETTINGS_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            ProfileSettings(navigation = navController)
                        }
                        composable(
                            Routes.DISPLAY_SETTINGS_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            DisplaySettings(navigation = navController)
                        }
                        composable(
                            Routes.GOALS_SETTINGS_SCREEN,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            GoalsSettings(navigation = navController, db)
                        }
                    }
                }
            }
        })
    }
}