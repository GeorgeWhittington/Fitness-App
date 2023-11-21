package com.foxden.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.database.DBHelper
import com.foxden.fitnessapp.ui.ActivityJournalScreen
import com.foxden.fitnessapp.ui.ActivityRecordingScreen
import com.foxden.fitnessapp.ui.DBTestScreen
import com.foxden.fitnessapp.ui.HomeScreen
import com.foxden.fitnessapp.ui.NutritionTrackingScreen
import com.foxden.fitnessapp.ui.SettingsScreen
import com.foxden.fitnessapp.ui.theme.FitnessAppTheme

object Routes {
    const val HOME_SCREEN = "HomeScreen"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ACTIVITY_JOURNAL_SCREEN = "ActivityJournalScreen"
    const val ACTIVITY_RECORDING_SCREEN = "ActivityRecordingScreen"
    const val NUTRITION_TRACKING_SCREEN = "NutritionTrackingScreen"

    const val DBTEST_SCREEN = "DBTestScreen"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get Database
        val db = DBHelper(this)


        setContent {
            val navController = rememberNavController()

            FitnessAppTheme {
                NavHost(navController = navController, startDestination = Routes.HOME_SCREEN ) {
                    composable(Routes.HOME_SCREEN) {
                        HomeScreen(navController, application)
                    }
                    composable(Routes.ACTIVITY_JOURNAL_SCREEN) {
                        ActivityJournalScreen(navController)
                    }
                    composable(Routes.ACTIVITY_RECORDING_SCREEN) {
                        ActivityRecordingScreen(navController)
                    }
                    composable(Routes.NUTRITION_TRACKING_SCREEN) {
                        NutritionTrackingScreen(navController)
                    }
                    composable(Routes.SETTINGS_SCREEN) {
                        SettingsScreen(navController)
                    }

                    composable(Routes.DBTEST_SCREEN) {
                        DBTestScreen(navController, db)
                    }
                }
            }
        }
    }
}