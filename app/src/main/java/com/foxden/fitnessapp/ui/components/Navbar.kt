package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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

sealed class Screen(val route: String, val icon: ImageVector, val contentDesc: String) {
    object Home : Screen(Routes.HOME_SCREEN, Icons.Outlined.Home, "Home Page")
    object ActivityJournal : Screen(Routes.ACTIVITY_JOURNAL_SCREEN, Icons.Outlined.FitnessCenter, "Activity Journal")
    object ActivityRecording : Screen(Routes.ACTIVITY_RECORDING_SCREEN, Icons.Outlined.RadioButtonChecked, "Activity Recording")
    object NutritionTracking : Screen(Routes.NUTRITION_TRACKING_SCREEN, Icons.Outlined.Restaurant, "Nutrition Tracking")
    object Settings : Screen(Routes.SETTINGS_SCREEN, Icons.Outlined.Settings, "Settings")
}

// TODO: Exclude nutrition tracking from this list based on settings!
val navItems = listOf(
    Screen.Home,
    Screen.ActivityJournal,
    Screen.ActivityRecording,
    Screen.NutritionTracking,
    Screen.Settings
)

@Composable
fun NavIconButton(navigation: NavController, currentDestination: NavDestination?, screen: Screen) {
    val iconButtonModifier = Modifier.padding(horizontal = 10.dp)
    val iconModifier = Modifier.size(40.dp)

    IconButton(
        onClick = {
            navigation.navigate(screen.route) {
                // Pop up to the start destination of the graph to avoid building
                // up a large stack of destinations on the back stack
                popUpTo(navigation.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when reselecting
                // the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            } },
        modifier = iconButtonModifier,
    ) {
        Icon(
            screen.icon, contentDescription = screen.contentDesc,
            modifier = iconModifier, tint = navButtonSelected(currentDestination, screen.route)
        )
    }
}

@Composable
fun NavBar(navigation: NavController) {
    val navBackStackEntry by navigation.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar (
        backgroundColor = Color(64, 117, 156),
        modifier = Modifier.height(50.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            navItems.forEach { screen ->
                NavIconButton(navigation, currentDestination, screen)
            }
        }
    }
}

@Preview
@Composable
fun PreviewNavBar() {
    val navController = rememberNavController()
    NavBar(navigation = navController)
}