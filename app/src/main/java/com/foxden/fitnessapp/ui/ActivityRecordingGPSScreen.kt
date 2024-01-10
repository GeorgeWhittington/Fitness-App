package com.foxden.fitnessapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.DBHelper

@Composable
fun ActivityRecordingGPSScreen(activityTypeId: Int, navigation: NavController, dbHelper: DBHelper) {
    Scaffold (containerColor = MaterialTheme.colorScheme.secondary) {
        Column(modifier = Modifier.padding(it)) {
            Text("GPS Recording Screen")
        }
    }
}