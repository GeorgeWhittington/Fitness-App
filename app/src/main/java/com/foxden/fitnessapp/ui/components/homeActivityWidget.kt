package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.utils.formatDistance
import com.foxden.fitnessapp.utils.formatDuration

/*
HomeActivityWidget()

This widget is used in order to display collected data from activity log onto the home screen
 */
@Composable
fun HomeActivityWidget(activities: Int, distance: Float, duration: Int){

    //get distance unit from datastore
    val dataStoreManager = SettingsDataStoreManager(LocalContext.current)
    val distanceUnit by dataStoreManager.getSettingFlow(Settings.DISTANCE_UNIT).collectAsState(initial = "")


    val totalDistance: Float = formatDistance(distance, distanceUnit)
    val durationString = formatDuration(duration)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Activities", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$activities", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                }
                Column (
                    modifier = Modifier.weight(1f)
                ){
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Total distance", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$totalDistance $distanceUnit", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Total time", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = durationString, fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
