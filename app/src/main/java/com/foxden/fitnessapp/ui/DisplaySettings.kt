package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.ui.components.NavBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DisplaySettings(navigation: NavController) {
    var isModified by remember { mutableStateOf(false) }
    val HeightUnitOptions = listOf("Feet","Cm")
    var selectedHeightUnit by remember { mutableStateOf(HeightUnitOptions[0]) }

    val CalorieUnitOptions = listOf("kcal","kJ.")
    var selectedCalorieUnit by remember { mutableStateOf(CalorieUnitOptions[0]) }

    val WeightUnitOptions = listOf("Kg","lb.")
    var selectedWeightUnit by remember { mutableStateOf(WeightUnitOptions[0]) }

    val DistanceUnitOptions = listOf("Miles","Km")
    var selectedDistanceUnit by remember { mutableStateOf(DistanceUnitOptions[0]) }

    val CharacterOptions = listOf("Fox","Cat","Racoon")
    var selectedCharacter by remember { mutableStateOf(CharacterOptions[0]) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {BackIcon{navigation.navigate(Routes.SETTINGS_SCREEN)}},
                actions = {

                    SaveOption(isModified = isModified) {
                        // Perform save action here

                        isModified = false
                    }},
                backgroundColor = Color.White,
                modifier = Modifier.height(56.dp)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Display", color = Color.Black, fontSize = 20.sp)
            }

        },
        bottomBar = { NavBar(navigation = navigation) }
    ) {innerPadding->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimationsOption()
            CalorieOption()
            DarkModeOption()
            Spacer(modifier = Modifier.height(20.dp))
            DropdownOption(options = CharacterOptions,
                label = "Character",
                selectedOptionText = selectedCharacter,
                updateSelection = {newSelection -> selectedCharacter = newSelection }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(40.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Unit Selection" ,fontSize = 20.sp,)
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = DistanceUnitOptions, label = "Distance unit",
                selectedOptionText = selectedDistanceUnit,
                updateSelection = {newSelection -> selectedDistanceUnit = newSelection }
            )
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = WeightUnitOptions, label = "Weight unit",
                selectedOptionText = selectedWeightUnit,
                updateSelection = {newSelection -> selectedWeightUnit = newSelection }
            )
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = CalorieUnitOptions, label = "Calorie unit",
                selectedOptionText = selectedCalorieUnit,
                updateSelection = {newSelection -> selectedCalorieUnit = newSelection }
            )
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = HeightUnitOptions, label = "Height unit",
                selectedOptionText = selectedHeightUnit,
                updateSelection = {newSelection -> selectedHeightUnit = newSelection }
            )
        }
    }
}






@Composable
fun AnimationsOption() {
    var checkedState by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            //.padding( vertical = 5.dp)
    ) {
        Text(
            text = "Animations", color = Color.Black, fontSize = 16.sp,
            modifier = Modifier.weight(1f))
        Switch(checked = checkedState, onCheckedChange = { checkedState = it })
    }
}
@Preview
@Composable
fun CalorieOption() {
    var checkedState by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        //.padding( vertical = 5.dp)
    ) {
        Text(
            text = "Calorie/Nutrition Tracking",
            color = Color.Black, fontSize = 16.sp,
            modifier = Modifier.weight(1f))
        Switch(
            checked = checkedState,
            onCheckedChange = { checkedState = it }
        )
    }
}

@Composable
fun DarkModeOption() {
    var checkedState by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        //.padding( vertical = 5.dp)
    ) {
        Text(
            text = "Dark mode", color = Color.Black, fontSize = 16.sp,
            modifier = Modifier.weight(1f))
        Switch(checked = checkedState, onCheckedChange = { checkedState = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownOption(options: List<String>, label: String, selectedOptionText: String, updateSelection: (newSelection: String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .clickable(onClick = { expanded = false })
                .fillMaxWidth(), readOnly = true,
            value = selectedOptionText, onValueChange = {}, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {selectionOption ->
                DropdownMenuItem(

                    text = { Text(selectionOption, fontFamily = FontFamily.SansSerif, fontSize = 16.sp) },
                    onClick = { updateSelection(selectionOption); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}