package com.foxden.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.database.DBHelper
import com.foxden.fitnessapp.ui.DBTestScreen
import com.foxden.fitnessapp.ui.HomeScreen
import com.foxden.fitnessapp.ui.LoginScreen
import com.foxden.fitnessapp.ui.MainSettings
import com.foxden.fitnessapp.ui.theme.FitnessAppTheme

object Routes {
    const val HOME_SCREEN = "HomeScreen"
    const val LOGIN_SCREEN = "LoginScreen"
    const val DBTEST_SCREEN = "DBTestScreen"
    const val MAIN_SETTINGS_SCREEN = "MainSettings"
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
                        HomeScreen(navigation = navController, application)
                    }
                    composable(Routes.LOGIN_SCREEN) {
                        LoginScreen(navigation = navController)
                    }
                    composable(Routes.DBTEST_SCREEN) {
                        DBTestScreen(navigation = navController, db)
                    }
                    composable(Routes.MAIN_SETTINGS_SCREEN) {
                        MainSettings(navigation = navController)
                    }
                }
            }
        }
    }
}