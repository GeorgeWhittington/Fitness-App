package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.AddActivityAttachmentsWidget
import com.foxden.fitnessapp.utils.saveImageToInternalStorage
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddManualActivityScreen(navigation: NavController, dbHelper: DBHelper) {
    // form data
    var title by remember { mutableStateOf("") }
    var activityType: ActivityType? by remember { mutableStateOf(null) }
    var notes by remember { mutableStateOf("") }
    var datetime: ZonedDateTime? by remember { mutableStateOf(null) }
    var duration: Pair<Int?, Int?>? by remember { mutableStateOf(null) }
    var distance: String by remember { mutableStateOf("0") }
    val imageURIs: MutableList<Uri> = remember { mutableStateListOf() }
    val activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }

    // get unit preference
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
    val distanceUnit by dataStoreManager.getSettingFlow(Settings.DISTANCE_UNIT).collectAsState(initial = "")

    // other state
    var activityTypeExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond() * 1000
    )
    val timePickerState = rememberTimePickerState()
    var datePickerExpanded by remember { mutableStateOf(false) }
    var timePickerExpanded by remember { mutableStateOf(false) }
    var durationPickerExpanded by remember { mutableStateOf(false) }

    // form dialog boxes
    if (datePickerExpanded) {
        DatePickerDialog(
            onDismissRequest = { datePickerExpanded = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerExpanded = false
                    timePickerExpanded = true
                }) { Text("Ok") }
            },
            dismissButton = { TextButton(onClick = { datePickerExpanded = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (timePickerExpanded) {
        AlertDialog(
            onDismissRequest = { timePickerExpanded = false},
            confirmButton = {
                TextButton(onClick = {
                    timePickerExpanded = false

                    val localDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(
                        ZoneId.systemDefault()).toLocalDate()

                    datetime = ZonedDateTime.of(
                        localDate,
                        LocalTime.of(timePickerState.hour, timePickerState.minute),
                        ZoneId.systemDefault()
                    )
                }) { Text("Ok") }
            },
            dismissButton = { TextButton(onClick = { timePickerExpanded = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePickerState) }
        )
    }

    if (durationPickerExpanded) {
        DurationPicker(
            initialHours = duration?.first, initialMinutes = duration?.second,
            onDismiss = { durationPickerExpanded = false }, onConfirm = { duration = it }
        )
    }

    // main form
    Scaffold (
        topBar = {
            Column {
                Box (modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 15.dp, end = 15.dp)) {
                    IconButton(onClick = { navigation.popBackStack() }) {
                        Icon(
                            Icons.Outlined.ChevronLeft, "Go Back",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text("Add Manual Activity", fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
            }
        }
    ) { scaffoldingPaddingValues ->
        Column (modifier = Modifier
            .padding(scaffoldingPaddingValues)
            .padding(start = 15.dp, end = 15.dp)
            .verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(10.dp))
            // Activity Title
            OutlinedTextField(
                value = title, onValueChange = { title = it }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), label = { Text("Activity Title") }
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Activity Type
            ExposedDropdownMenuBox(
                expanded = activityTypeExpanded, onExpandedChange = {activityTypeExpanded = it},
                modifier = Modifier.fillMaxWidth()
            ) {
                var leadingIcon: (@Composable () -> Unit)? = null
                if (activityType != null)
                    leadingIcon = @Composable { Icon(Constants.ActivityIcons.values()[activityType!!.iconId].image, activityType!!.name) }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = activityType?.name ?: "", onValueChange = {}, readOnly = true,
                    label = { Text("Activity Type") }, leadingIcon = leadingIcon,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityTypeExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = activityTypeExpanded,
                    onDismissRequest = { activityTypeExpanded = false }
                ) {
                    activityTypeList.forEach {
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Icon(Constants.ActivityIcons.values()[it.iconId].image, it.name)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(it.name)
                                }
                            },
                            onClick = { activityType = it; activityTypeExpanded = false },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Activity Notes
            OutlinedTextField(
                value = notes, onValueChange = { notes = it }, label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(), minLines = 5
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Activity datetime
            var datetimeString = ""
            if (datetime != null)
                datetimeString = datetime!!.format(DateTimeFormatter.ofPattern("d LLLL yyyy, hh:mma"))

            OutlinedTextField(
                value = datetimeString, onValueChange = {}, label = { Text("Time") },
                trailingIcon = { Icon(Icons.Outlined.CalendarMonth, null) },
                readOnly = true, modifier = Modifier.fillMaxWidth(),
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    datePickerExpanded = true
                                }
                            }
                        }
                    }

            )
            Spacer(modifier = Modifier.height(10.dp))

            // Activity Duration
            var durationString by remember { mutableStateOf("") }
            LaunchedEffect(duration) {
                if (duration != null) {
                    var str = ""
                    if (duration!!.first != null && duration!!.first != 0) {
                        str += "${duration!!.first} Hour"
                        if (duration!!.first!! > 1) { str += "s" }
                    }
                    if (duration!!.second != null && duration!!.second != 0) {
                        str += " ${duration!!.second} Minute"
                        if (duration!!.second!! > 1) { str += "s" }
                    }
                    durationString = str.trim()
                }
            }

            OutlinedTextField(
                value = durationString, onValueChange = {}, label = { Text("Duration") },
                trailingIcon = { Icon(Icons.Outlined.UnfoldMore, null) },
                readOnly = true, modifier = Modifier.fillMaxWidth(),
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    durationPickerExpanded = true
                                }
                            }
                        }
                    }

            )
            Spacer(modifier = Modifier.height(10.dp))

            // Activity Distance
            OutlinedTextField(
                value = distance, suffix = { Text(distanceUnit.toString()) }, label = { Text("Distance" )},
                trailingIcon = { Icon(Icons.Outlined.UnfoldMore, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused)
                            return@onFocusChanged

                        try {
                            val distanceFloat = distance.toFloat()
                            distance = if (distanceFloat == 0.0f) {
                                ""
                            } else {
                                distanceFloat.toString()
                            }
                        } catch (_: NumberFormatException) {
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { input ->
                    if (input == "") {
                        distance = input
                        return@OutlinedTextField
                    }

                    val numRegex = Regex("[\\d.]+")
                    if (!(input matches numRegex))
                        return@OutlinedTextField

                    if (input.count { it == '.' } > 1)
                        return@OutlinedTextField

                    distance = input
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Activity Attachments
            AddActivityAttachmentsWidget(
                imageURIs = imageURIs, addImageUri = {uri -> imageURIs.add(uri)},
                removeImageUri = {uriIndex -> imageURIs.removeAt(uriIndex)}
            )

            // Add Activity
            Button(onClick = {
                var distanceFloat: Float = if (distance.isEmpty()) 0.0f else distance.toFloat()
                if (distanceUnit == "Km") { distanceFloat /= 1.609f }

                var d1 = 0
                if (duration != null) {
                    if (duration!!.first != null) {
                        d1 += duration!!.first?.times(3600) ?: 0
                    }

                    if (duration!!.second != null) {
                        d1 += duration!!.second?.times(60) ?: 0
                    }
                }

                if (AddManualActivity(dbHelper, context, title, activityType, notes, datetime, d1, distanceFloat, imageURIs)) {
                    navigation.popBackStack()
                }

            }) {
                Text("Add Activity")
            }
        }
    }
}

@Composable
fun DurationPicker(
    initialHours: Int?, initialMinutes: Int?,
    onDismiss: () -> Unit, onConfirm: (Pair<Int, Int>) -> Unit
) {
    var durationHours by remember { mutableStateOf(if (initialHours != null && initialHours != 0) { initialHours.toString() } else {""}) }
    var durationMinutes by remember { mutableStateOf(if ((initialMinutes != null) && (initialMinutes != 0)) { initialMinutes.toString() } else {""}) }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val durationHoursInt = if (durationHours == "") { 0 } else { durationHours.toInt() }
                    val durationMinutesInt = if (durationMinutes == "") { 0 } else { durationMinutes.toInt() }

                    if (durationHoursInt > 0 || durationMinutesInt > 0)
                        onConfirm(Pair(durationHoursInt, durationMinutesInt))
                } catch (_: NumberFormatException) {}

                onDismiss()
            }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        text = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    modifier = Modifier.width(55.dp),
                    value = durationHours, onValueChange = {
                        if (it == "" || it == "0")
                            durationHours = ""

                        if (it.length > 2)
                            return@OutlinedTextField

                        try {
                            durationHours = it.toInt().toString()
                        } catch (nfe: NumberFormatException) {
                            return@OutlinedTextField
                        }
                    },
                    singleLine = true, textStyle = TextStyle(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Text("Hour(s)", fontSize = 18.sp)
                OutlinedTextField(
                    modifier = Modifier.width(55.dp),
                    value = durationMinutes, onValueChange = {
                        if (it == "" || it == "0")
                            durationMinutes = ""

                        if (it.length > 2)
                            return@OutlinedTextField

                        try {
                            val intInput = it.toInt()
                            if (intInput >= 60)
                                return@OutlinedTextField
                            durationMinutes = intInput.toString()
                        } catch (nfe: NumberFormatException) {
                            return@OutlinedTextField
                        }
                    },
                    singleLine = true, textStyle = TextStyle(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text("Minute(s)", fontSize = 18.sp)
            }
        }
    )
}

private fun ValidateForm(title: String?, activityType: ActivityType?, startTime: ZonedDateTime?, duration: Int?, distance: Float?) : Boolean {
    if (title.isNullOrEmpty())
        return false

    if (activityType == null)
        return false

    if (startTime == null)
        return false

    if (duration == null || duration <= 0)
        return false

    if (activityType.gpsEnabled && (distance == null || distance <= 0.0)) {
        return false
    }

    return true
}

@RequiresApi(Build.VERSION_CODES.O)
private fun AddManualActivity(dbHelper: DBHelper, context: Context, title: String?, activityType: ActivityType?, notes: String?, startTime: ZonedDateTime?, duration: Int, distance: Float?, imageUris: MutableList<Uri>) : Boolean {
    if (!ValidateForm(title, activityType, startTime, duration, distance))
        return false

    val savedUris = mutableListOf<String>()
    imageUris.forEach {
        savedUris.add(saveImageToInternalStorage(context, it))
    }

    val log = ActivityLog(
        title=title!!,
        activityTypeId = activityType!!.id,
        notes = notes!!,
        startTime = startTime!!.toEpochSecond(),
        duration = duration,
        distance = distance!!,
        calories = duration / 60 * 65 * 7, // not very accurate
        images = savedUris
    )

    if (!ActivityLogDAO.insert(dbHelper.writableDatabase, log)) {
        Log.d("FIT", "Failed to insert activity log into the database")
        return false
    }

    Log.d("FIT", "Inserted activity into the database!")
    return true
}