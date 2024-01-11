package com.foxden.fitnessapp.ui

import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.foxden.fitnessapp.ui.components.AddActivityAttachmentsWidget
import com.foxden.fitnessapp.utils.saveImageToInternalStorage
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

private class Set(val setNumber: Int, val totalSeconds: Int)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityRecordingNoGPSScreen(activityTypeId: Int, navigation: NavController, dbHelper: DBHelper) {
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
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

    var showFinalisationPopUp by remember { mutableStateOf(false) }

    fun addActivity(title: String?, notes: String?, imageURIs: List<Uri>?) {
        if (title == null || notes == null || imageURIs == null || selectedActivity == null) {
            navigation.popBackStack()
            return
        }

        val duration = if (selectedActivity.setsEnabled) completedSets.sumOf { it.totalSeconds } else ticks
        val savedUris = mutableListOf<String>()
        imageURIs.forEach {
            savedUris.add(saveImageToInternalStorage(context, it))
        }

        ActivityLogDAO.insert(dbHelper.writableDatabase, ActivityLog(
            title = title,
            activityTypeId = selectedActivity.id,
            notes = notes,
            startTime = startTime.toEpochSecond(),
            duration = duration,
            distance = 0.0f,
            calories = duration / 60 * 65 * 7,
            images = savedUris
        ))
        navigation.popBackStack()
    }

    if (showFinalisationPopUp) {
        val notes = if (selectedActivity?.setsEnabled == true) {
            var note = ""
            for (set: Set in completedSets) {
                val time = String.format("%01d:%02d:%02d", set.totalSeconds / 3600, (set.totalSeconds % 3600) / 60, set.totalSeconds % 60)
                note += "Set ${set.setNumber}: ${time}\n"
            }
            note
        } else {
            ""
        }

        FinaliseActivityPopUp(
            initialTitle = selectedActivity!!.name,
            initialNotes = notes,
            onDismiss = {title_, notes_, uris_ -> addActivity(title_, notes_, uris_)})
    }
    
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
                        if (selectedActivity?.setsEnabled == true) {
                            completedSets.add(Set(currentSet, ticks))
                        }
                        timerPaused = true
                        showFinalisationPopUp = true
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

@Composable
fun FinaliseActivityPopUp(initialTitle: String, initialNotes: String, onDismiss: (title: String?, notes: String?, uris: List<Uri>?) -> Unit) {
    var title by remember { mutableStateOf(initialTitle) }
    var notes by remember { mutableStateOf(initialNotes) }
    val imageURIs: MutableList<Uri> = remember { mutableStateListOf() }

    AlertDialog(
        title = { Text(text = "Add Recorded Activity") },
        onDismissRequest = { onDismiss(null, null, null) },
        confirmButton = { Button(onClick = {
            if (title.isEmpty()) {
                return@Button
            }

            onDismiss(title, notes, imageURIs)
        }) { Text("Confirm") } },
        dismissButton = { Button(onClick = { onDismiss(null, null, listOf()) }) { Text("Discard") } },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), label = { Text("Activity Title") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it }, label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(), minLines = 5
                )
                Spacer(modifier = Modifier.height(10.dp))
                AddActivityAttachmentsWidget(
                    imageURIs = imageURIs, addImageUri = { uri -> imageURIs.add(uri) },
                    removeImageUri = { uriIndex -> imageURIs.removeAt(uriIndex) }
                )
            }
        }
    )
}