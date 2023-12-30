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

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NutritionAddPresetScreen(navigation: NavController, dbHelper: DBHelper) {

    var datetime: ZonedDateTime? by remember { mutableStateOf(ZonedDateTime.now()) }
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf(0) }

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
                    androidx.compose.material3.Text("Add Preset Meal", fontSize = 20.sp, modifier = Modifier.align(
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

                // Activity Title
                OutlinedTextField(
                    value = name, onValueChange = { name = it }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), label = { androidx.compose.material3.Text("Name") }
                )
                Spacer(modifier = Modifier.height(10.dp))

                // -- Calories -- //
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
                    if (AddPresetMeal(dbHelper, name, calories)) {
                        navigation.navigate(Routes.NUTRITION_TRACKING_SCREEN)
                    }
                }) {
                    androidx.compose.material3.Text("Add Preset Meal")
                }

            }
        }
    }
}

private fun AddPresetMeal(dbHelper: DBHelper, name: String, calories: Int) : Boolean
{
    if (calories <= 0)
        return false

    var preset = NutritionMealPreset()
    preset.name = name
    preset.calories = calories

    if (!NutritionMealPresetDAO.insert(dbHelper.writableDatabase, preset)) {
        Log.d("FIT", "Failed to insert activity log into the database")
        return false
    }

    Log.d("FIT", "Inserted activity into the database!")
    return true
}