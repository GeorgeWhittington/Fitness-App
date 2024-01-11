package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionLogDAO
import com.foxden.fitnessapp.data.NutritionMealPreset
import com.foxden.fitnessapp.data.NutritionMealPresetDAO
import com.foxden.fitnessapp.data.NutritionType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/*  Nutrition Log Meal Screen 
    Allows the user to log a meal to track
*/
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NutritionLogMealScreen(navigation: NavController, dbHelper: DBHelper) {
    
    // State for the input, with default values
    var selectedMealType by remember { mutableStateOf(NutritionType.BREAKFAST) }
    var selectedMealTypeExpanded by remember { mutableStateOf(false) }
    var datetime: ZonedDateTime? by remember { mutableStateOf(ZonedDateTime.now()) }
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf(0) }

    var mealPresetList = remember {
        NutritionMealPresetDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }
    var mealPresetExpanded by remember { mutableStateOf(false) }
    var mealPreset: NutritionMealPreset? by remember { mutableStateOf(null) }
    var datePickerExpanded by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = ZonedDateTime.now().toEpochSecond() * 1000
    )

    // Date picker component
    if (datePickerExpanded) {
        DatePickerDialog(
            onDismissRequest = { datePickerExpanded = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerExpanded = false

                    val localDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(
                        ZoneId.systemDefault()).toLocalDate()

                    datetime = ZonedDateTime.of(
                        localDate,
                        LocalTime.of(0, 0),
                        ZoneId.systemDefault()
                    )
                }) { androidx.compose.material3.Text("Ok") }
            },
            dismissButton = { TextButton(onClick = { datePickerExpanded = false }) { androidx.compose.material3.Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold (
        topBar = {
            Column {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 15.dp, end = 15.dp)
                ) {
                    IconButton(onClick = { navigation.popBackStack() }) {
                        Icon(
                            Icons.Outlined.ChevronLeft, "Go Back",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    androidx.compose.material3.Text("Log Meal", fontSize = 20.sp, modifier = Modifier.align(
                        Alignment.Center))
                }
                Spacer(modifier = Modifier.height(10.dp))

                Divider()
            }
        }
    ) { scaffoldingPaddingValues ->
        Column(modifier = Modifier.padding(scaffoldingPaddingValues)) {
            Column(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Meal type comments
                ExposedDropdownMenuBox(
                    expanded = selectedMealTypeExpanded, onExpandedChange = {selectedMealTypeExpanded = it},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var leadingIcon: (@Composable () -> Unit)? = null

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = selectedMealType.name ?: "", onValueChange = {}, readOnly = true,
                        label = { androidx.compose.material3.Text("Activity Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectedMealTypeExpanded) },
                        leadingIcon = leadingIcon
                    )
                    ExposedDropdownMenu(
                        expanded = selectedMealTypeExpanded,
                        onDismissRequest = { selectedMealTypeExpanded = false }
                    ) {

                        NutritionType.values().forEach {
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        androidx.compose.material3.Text(it.name)
                                    }
                                },
                                onClick = { selectedMealType = it; selectedMealTypeExpanded = false },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }

                    }
                }

                // Date time picker component
                OutlinedTextField(
                    value = datetime!!.format(DateTimeFormatter.ofPattern("d LLLL yyyy")), onValueChange = {}, label = {
                        androidx.compose.material3.Text(
                            "Time"
                        )
                    },
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

                //  Preset meal dropdown box - allows the user to automatically fill calories from presets
                ExposedDropdownMenuBox(
                    expanded = mealPresetExpanded, onExpandedChange = {mealPresetExpanded = it},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var leadingIcon: (@Composable () -> Unit)? = null

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        value = mealPreset?.name ?: "Custom", onValueChange = {}, readOnly = true,
                        label = { androidx.compose.material3.Text("Meal Preset") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealPresetExpanded) },
                        leadingIcon = leadingIcon
                    )
                    ExposedDropdownMenu(
                        expanded = mealPresetExpanded,
                        onDismissRequest = { mealPresetExpanded = false }
                    ) {

                        mealPresetList.forEach {
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        androidx.compose.material3.Text(it.name)
                                    }
                                },
                                onClick = {
                                    mealPreset = it;
                                    mealPresetExpanded = false;
                                    calories = it.calories
                                          },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }

                    }
                }

                // Calories field
                OutlinedTextField(
                    value = calories.toString(), suffix = { androidx.compose.material3.Text("kcal") }, label = {
                        androidx.compose.material3.Text(
                            "calories"
                        )
                    },
                    trailingIcon = { Icon(Icons.Outlined.UnfoldMore, null) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { input ->
                        mealPreset = null
                        var parsed = input.toIntOrNull()

                        if (parsed != null) {
                            calories = parsed
                        } else {
                            calories = 0
                        }

                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Log meal 
                Button(onClick = {
                    if (AddNutritionLog(dbHelper, selectedMealType, datetime, calories)) {
                        navigation.navigate(Routes.NUTRITION_TRACKING_SCREEN)
                    }

                }) {
                    androidx.compose.material3.Text("Log Meal")
                }

            }
        }
    }
}

/* Attempt to add a nutrition log into the database, returns if successful */
@RequiresApi(Build.VERSION_CODES.O)
private fun AddNutritionLog(dbHelper: DBHelper, type: NutritionType?, date: ZonedDateTime?, calories: Int?) : Boolean
{
    if (type == null || date == null || calories == null || calories <= 0)
        return false

    var l = NutritionLog()
    l.type = type
    l.date = date.toLocalDate()
    l.calories = calories

    if (!NutritionLogDAO.insert(db = dbHelper.writableDatabase, l)) {
        return false
    }

    Log.d("FIT", "Inserted Successfully!")
    return true
}