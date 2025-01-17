package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalType
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.theme.Orange
import com.foxden.fitnessapp.ui.theme.Yellow
import com.foxden.fitnessapp.utils.formatDistance


/*
HomeGoalWidget()

This widget is used in order to display collected data from activities to track goals

 */
@Composable
fun HomeGoalWidget(goal: Goal, sumDistance: Float, activityType: ActivityType){
    val dataStoreManager = SettingsDataStoreManager(LocalContext.current)
    val distanceUnit by dataStoreManager.getSettingFlow(Settings.DISTANCE_UNIT).collectAsState(initial = "")

    val totalDistance = formatDistance(sumDistance, distanceUnit)
    val goalDistance = formatDistance(goal.distance, distanceUnit)

    Row (modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(size = 10.dp))
        .background(if (totalDistance >= goalDistance) Orange else Yellow)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (goal.type == GoalType.DISTANCE) {

                            if (totalDistance >= goalDistance) {
                                Text(text = "Goal completed!", fontSize = 20.sp)
                                Text("$goalDistance $distanceUnit/$goalDistance $distanceUnit")
                            } else {
                                Text(text = "Total Distance:")
                                Text("$totalDistance $distanceUnit/$goalDistance $distanceUnit")
                            }
                        }
                    }
                Spacer(modifier = Modifier.weight(1f))

                Column {
                    Icon(
                        Constants.ActivityIcons.values()[activityType.iconId].image,
                        contentDescription = activityType.name,
                        modifier = Modifier.size(50.dp)
                    )
                    Text(text = activityType.name, fontSize = 16.sp)
                }
            }
        }
    }
}