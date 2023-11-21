package com.foxden.fitnessapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.Routes

fun navButtonSelected(currentDestination: NavDestination?, route: String) : Color {
    val selectedColor = Color(134, 187, 216)
    val contentColor = Color(255, 255, 255)

    return if (currentDestination?.hierarchy?.any { it.route == route } == true) {
        selectedColor
    } else {
        contentColor
    }
}

@Composable
fun NavBar(navigation: NavController) {
    val iconButtonModifier = Modifier.padding(horizontal = 10.dp)
    val iconModifier = Modifier.size(40.dp)

    val navBackStackEntry by navigation.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar (backgroundColor = Color(64, 117, 156)) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { navigation.navigate(Routes.HOME_SCREEN) },
                modifier = iconButtonModifier,
            ) {
                Icon(
                    Icons.Outlined.Home, contentDescription = "Home Page",
                    modifier = iconModifier, tint = navButtonSelected(currentDestination, Routes.HOME_SCREEN)
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = iconButtonModifier
            ) {
                Icon(
                    Icons.Outlined.FitnessCenter, contentDescription = "Activity Journal",
                    modifier = iconModifier,
                    tint = Color(255, 255, 255)
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = iconButtonModifier
            ) {
                Icon(Icons.Outlined.RadioButtonChecked, contentDescription = "Activity Recording", modifier = iconModifier, tint = Color(255, 255, 255))
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = iconButtonModifier
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = "Nutrition Tracking", modifier = iconModifier, tint = Color(255, 255, 255))
            }
            IconButton(
                onClick = { navigation.navigate(Routes.MAIN_SETTINGS_SCREEN) },
                modifier = iconButtonModifier
            ) {
                Icon(
                    Icons.Outlined.Settings, contentDescription = "Settings",
                    modifier = iconModifier, tint = navButtonSelected(currentDestination, Routes.MAIN_SETTINGS_SCREEN)
                )
            }
        }
    }
}

@Preview
@Composable
fun previewNavBar() {
    val navController = rememberNavController()
    NavBar(navigation = navController)
}