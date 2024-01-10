package com.foxden.fitnessapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

private class Set(val setNumber: Int, val totalSeconds: Int)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityRecordingNoGPSScreen(activityTypeId: Int, navigation: NavController, dbHelper: DBHelper) {
    val dataStoreManager = SettingsDataStoreManager(LocalContext.current)
    val caloriesEnabled by dataStoreManager.getSettingFlow(Settings.CALORIES_ENABLED).collectAsState(initial = true)
    val calorieUnit by dataStoreManager.getSettingFlow(Settings.CALORIE_UNIT).collectAsState(initial = "")

    val activityList = remember { ActivityTypeDAO.fetchAll(dbHelper.readableDatabase).toMutableStateList() }
    val selectedActivity: ActivityType? = try {
        activityList.first { log -> log.id == activityTypeId }
    } catch (e: NoSuchElementException) {
        // no or invalid activity type passed, exit immediately
        navigation.popBackStack()
        null
    }

    val startTime: ZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }
    var timerPaused by remember { mutableStateOf(false) }
    var ticks by remember { mutableStateOf(0) }
    var currentSet by remember { mutableStateOf(1) }
    val completedSets = remember { mutableStateListOf<Set>() }
    val totalSeconds = ticks + completedSets.sumOf { it.totalSeconds }
    val timeString = String.format("%01d:%02d:%02d", ticks / 3600, (ticks % 3600) / 60, ticks % 60)
    val calorieString = String.format("%d", totalSeconds / 60 * 65 * 7)
    LaunchedEffect(Unit) {
        while(true) {
            delay(1.seconds)
            if (!timerPaused)
                ticks++
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val rectangleContainerModifier = Modifier
        .fillMaxWidth()
        .height(116.dp)
        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))

    Scaffold (containerColor = MaterialTheme.colorScheme.secondary) {
        Box(modifier = Modifier
            .padding(it)
            .padding(start = 10.dp, end = 10.dp)
            .fillMaxSize()
        ) {
            // center vertically
            Column (modifier = Modifier.align(Alignment.CenterStart)) {
                if (selectedActivity?.setsEnabled == true) {
                    Box (modifier = rectangleContainerModifier) {
                        Text(
                            "Set $currentSet", color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 64.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(60.dp),
                            onClick = {
                                completedSets.add(Set(currentSet, ticks))
                                currentSet++
                                ticks = 0
                            }
                        ) {
                            Icon(
                                Icons.Outlined.ChevronRight, "Next Set",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Box (modifier = rectangleContainerModifier) {
                    Text(
                        timeString, color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.align(Alignment.Center), fontSize = 64.sp,
                    )
                }
                if (caloriesEnabled == true) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box (modifier = rectangleContainerModifier) {
                        Text(
                            calorieString, color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.align(Alignment.Center), fontSize = 64.sp
                        )
                        Text(
                            calorieUnit.toString(), color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(10.dp)
                        )
                    }
                }
            }

            // pause and stop controls, align to bottom of box
            Box (modifier = Modifier
                .align(Alignment.BottomCenter)
                .width((screenWidth / 3) * 2)
                .padding(bottom = 10.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(55.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    onClick = {
                        /*TODO: stop and save activity */
                        // Because I Don't want to add extra field data to activities, I'm going to save the set data
                        // in the notes field. something like "set 1: 00:10:11, set 2: 00:20:10" etc etc
                        // and then ovs the actual data saved to the duration is just all of them summed
                        var log: ActivityLog

                        // show dialog where you can edit:
                        // title
                        // notes (set data will be pre-filled at top if applicable)

                        if (selectedActivity?.setsEnabled == true) {
                            completedSets.add(Set(currentSet, ticks))
                            log = ActivityLog(
                                title = selectedActivity.name, // TODO: replace
                                activityTypeId = selectedActivity.id,
                                notes = "", // TODO: replace
                                startTime = startTime.toEpochSecond(),
                                duration = completedSets.sumOf { it.totalSeconds },
                                distance = 0.0f,
                                calories = completedSets.sumOf { it.totalSeconds } / 60 * 65 * 7
                            )
                        } else {
                            log = ActivityLog(
                                title = selectedActivity!!.name, // TODO: replace
                                activityTypeId = selectedActivity.id,
                                notes = "", // TODO: replace
                                startTime = startTime.toEpochSecond(),
                                duration = ticks,
                                distance = 0.0f,
                                calories = ticks / 60 * 65 * 7
                            )
                        }

                        ActivityLogDAO.insert(dbHelper.writableDatabase, log)
                        navigation.popBackStack()
                    }
                ) {
                    Icon(
                        Icons.Filled.Stop, "Finish Activity",
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(50.dp)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(85.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    onClick = { timerPaused = !timerPaused }
                ) {
                    if (timerPaused) {
                        Icon(
                            Icons.Filled.PlayArrow, "Resume Activity",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Pause, "Pause Activity",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }
        }
    }
}