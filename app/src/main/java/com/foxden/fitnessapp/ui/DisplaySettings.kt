package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.NavBar
import kotlinx.coroutines.flow.first


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DisplaySettings(navigation: NavController) {
    val context = LocalContext.current

    //Saving and retrieving data:
        //used to save option
        var isModified by remember { mutableStateOf(false) }
        //link to datastore
        val dataStoreManager = SettingsDataStoreManager(context)
        //used for saving the data to datastore
        val triggerSave = remember { mutableStateOf(false) }
        val saveSwitch = remember { mutableStateOf(false) }


    //Switch options:
        var animationsSwitch by rememberSaveable { mutableStateOf(false) }
        var CalorieSwitch by rememberSaveable { mutableStateOf(false) }
        var DarkmodeSwitch by rememberSaveable { mutableStateOf(false) }

    //Dropdown options:

        val HeightUnitOptions = listOf("Feet","Cm")
        var currentHeightUnit by rememberSaveable { mutableStateOf("") }

        val CalorieUnitOptions = listOf("kcal","kJ.")
        var currentCalorieUnit by rememberSaveable { mutableStateOf("") }

        val WeightUnitOptions = listOf("Kg","lb.")
        var currentWeightUnit by rememberSaveable { mutableStateOf("") }

        val DistanceUnitOptions = listOf("Miles","Km")
        var currentDistanceUnit by rememberSaveable { mutableStateOf("") }

        val CharacterOptions = listOf("Fox","Cat","Racoon")
        var currentCharacter by rememberSaveable { mutableStateOf("") }

    //ensures data is only retrieved once per creation
    LaunchedEffect(Unit) {
        GetData(dataStoreManager,
            onAnimationOptionLoaded = { loadedAnimations ->
                animationsSwitch = loadedAnimations},
            onCalorieOptionLoaded = { loadedCalorie ->
                CalorieSwitch = loadedCalorie},
            onDarkmodeOptionLoaded = { loadedDarkmode ->
                DarkmodeSwitch = loadedDarkmode},

            onCharacterLoaded = { loadedCharacter ->
                currentCharacter = loadedCharacter
            },
            onDistanceUnitLoaded = { loadedDistanceUnit ->
                currentDistanceUnit = loadedDistanceUnit
            },
            onWeightUnitLoaded = { loadedWeightUnit ->
                currentWeightUnit = loadedWeightUnit
            },
            onCalorieUnitLoaded = { loadedCalorieUnit ->
                currentCalorieUnit = loadedCalorieUnit
            },
            onHeightUnitLoaded = { loadedHeightUnit ->
                currentHeightUnit = loadedHeightUnit
            }
        )
    }
    LaunchedEffect(saveSwitch.value) {
        if (saveSwitch.value) {
            dataStoreManager.saveSwitchSetting("AnimationsKey", animationsSwitch)
            dataStoreManager.saveSwitchSetting("CalorieKey", CalorieSwitch)
            dataStoreManager.saveSwitchSetting("DarkmodeKey", DarkmodeSwitch)

            saveSwitch.value = false
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {BackIcon{navigation.navigate(Routes.SETTINGS_SCREEN)}},
                actions = {

                    //if a change is detected, will give an option to save - launch effect
                    SaveOption(isModified = isModified) {

                        triggerSave.value = true
                        isModified = false
                    }
                    //saves the data to datastore
                    LaunchedEffect(triggerSave.value) {
                        if (triggerSave.value) {
                            dataStoreManager.saveStringSetting("CharacterKey", currentCharacter)
                            dataStoreManager.saveStringSetting("DistanceUnitKey", currentDistanceUnit)
                            dataStoreManager.saveStringSetting("WeightUnitKey", currentWeightUnit)
                            dataStoreManager.saveStringSetting("CalorieUnitKey", currentCalorieUnit)
                            dataStoreManager.saveStringSetting("HeightUnitKey", currentHeightUnit)
                            triggerSave.value = false
                        }
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

            //Switches:
            SettingSwitch(
                label = "Animations",
                isChecked = animationsSwitch,
                onCheckedChange = { newValue ->
                    animationsSwitch = newValue
                    saveSwitch.value = true
                }
            )
            SettingSwitch(
                label = "Calorie/Nutrition Tracking",
                isChecked = CalorieSwitch,
                onCheckedChange = { newValue ->
                    CalorieSwitch = newValue
                    saveSwitch.value = true
                }
            )
            SettingSwitch(
                label = "Dark mode",
                isChecked = DarkmodeSwitch,
                onCheckedChange = { newValue ->
                    DarkmodeSwitch = newValue
                    saveSwitch.value = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Create dropdowns, show options and current value
            DropdownOption(options = CharacterOptions,
                label = "Character",
                selectedOptionText = currentCharacter,
                updateSelection = {newSelection -> currentCharacter = newSelection }
            ){isModified = true}
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
                selectedOptionText = currentDistanceUnit,
                updateSelection = {newSelection -> currentDistanceUnit = newSelection }
            ){isModified = true}
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = WeightUnitOptions, label = "Weight unit",
                selectedOptionText = currentWeightUnit,
                updateSelection = {newSelection -> currentWeightUnit = newSelection }
            ){isModified = true}
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = CalorieUnitOptions, label = "Calorie unit",
                selectedOptionText = currentCalorieUnit,
                updateSelection = {newSelection -> currentCalorieUnit = newSelection }
            ){isModified = true}
            Spacer(modifier = Modifier.height(10.dp))
            DropdownOption(
                options = HeightUnitOptions, label = "Height unit",
                selectedOptionText = currentHeightUnit,
                updateSelection = {newSelection -> currentHeightUnit = newSelection }
            ){isModified = true}
        }
    }
}



//Used to retrieve stored data
suspend fun GetData (
    dataStoreManager: SettingsDataStoreManager,
    onAnimationOptionLoaded: (Boolean) -> Unit,
    onCalorieOptionLoaded: (Boolean) -> Unit,
    onDarkmodeOptionLoaded: (Boolean) -> Unit,
    onCharacterLoaded: (String) -> Unit,
    onDistanceUnitLoaded: (String) -> Unit,
    onWeightUnitLoaded: (String) -> Unit,
    onCalorieUnitLoaded: (String) -> Unit,
    onHeightUnitLoaded: (String) -> Unit
){
    val animationsSwitch = dataStoreManager.getSwitchSetting("AnimationsKey", true).first()
    onAnimationOptionLoaded(animationsSwitch)
    val calorieSwitch = dataStoreManager.getSwitchSetting("CalorieKey", true).first()
    onCalorieOptionLoaded(calorieSwitch)
    val darkmodeSwitch = dataStoreManager.getSwitchSetting("DarkmodeKey", false).first()
    onDarkmodeOptionLoaded(darkmodeSwitch)

    val character = dataStoreManager.getStringSetting("CharacterKey", "Fox").first()
    onCharacterLoaded(character)
    val distanceUnit = dataStoreManager.getStringSetting("DistanceUnitKey", "Miles").first()
    onDistanceUnitLoaded(distanceUnit)
    val weightUnit = dataStoreManager.getStringSetting("WeightUnitKey", "Kg").first()
    onWeightUnitLoaded(weightUnit)
    val calorieUnit = dataStoreManager.getStringSetting("CalorieUnitKey", "kcal").first()
    onCalorieUnitLoaded(calorieUnit)
    val heightUnit = dataStoreManager.getStringSetting("HeightUnitKey", "Feet").first()
    onHeightUnitLoaded(heightUnit)

}

@Composable
fun SettingSwitch(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownOption(options: List<String>, label: String, selectedOptionText: String, updateSelection: (newSelection: String) -> Unit,onChange: () -> Unit) {
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
                    onClick = {
                        updateSelection(selectionOption)
                        expanded = false
                        onChange()},
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
