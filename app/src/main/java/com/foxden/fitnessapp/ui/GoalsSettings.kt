package com.foxden.fitnessapp.ui
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalDAO
import com.foxden.fitnessapp.data.GoalFrequency
import com.foxden.fitnessapp.data.GoalType
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.GoalsWidget
import com.foxden.fitnessapp.ui.components.NavBar
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalsSettings(navigation: NavController, dbHelper: DBHelper) {
    val isDialogOpen = remember { mutableStateOf(false) }

    //link to datastore
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)

    //used for save option
    var isModified by remember { mutableStateOf(false) }

    //used for saving the data to datastore
    val triggerSave = remember { mutableStateOf(false) }
    var currentCalorieGoal by rememberSaveable { mutableIntStateOf(0) }
    var calorieChoice by rememberSaveable { mutableStateOf(false) }
    var distanceUnit by rememberSaveable { mutableStateOf("") }
    var goalAdded by rememberSaveable { mutableStateOf(false) }

    //get goals from database
    var GoalList = remember {
        GoalDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }
    var activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }

    //remove goals
    var showDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }

    // get data from datastore
    LaunchedEffect(Unit) {
        GetGoalsData(
            dataStoreManager,
            onCalorieGoalLoaded = { loadedCalorieGoal ->
                currentCalorieGoal = loadedCalorieGoal
            },
            onCalorieChoiceLoaded = { loadedCalorieChoice ->
                calorieChoice = loadedCalorieChoice
            },
            onDistanceUnitLoaded = { loadedDistanceUnit ->
                distanceUnit = loadedDistanceUnit
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackIcon { navigation.popBackStack() } },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.height(56.dp),
                actions = {
                    SaveOption(isModified = isModified) {
                        triggerSave.value = true
                        isModified = false
                    }
                    LaunchedEffect(triggerSave.value) {
                        if (triggerSave.value) {
                            dataStoreManager.saveIntSetting("CalorieGoalKey", currentCalorieGoal)
                            triggerSave.value = false
                        }
                    }}
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) { PageName(text = "Goals") }

        },
        bottomBar = { NavBar(navigation = navigation) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(calorieChoice){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Daily calorie goal", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    IntInputField(
                        icon = Icons.Outlined.Restaurant,
                        placeholder = "Daily calorie goal",
                        value = currentCalorieGoal,
                        onValueChange = { newValue -> currentCalorieGoal = newValue },
                        unit = "kcal",
                        min =  500,
                        max = 4000,
                        onChange = { isModified = true },
                        keyboardType = KeyboardType.Number
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "The recommended daily kcal for adults is 1500-2500 kcal daily",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if (isDialogOpen.value) {
                    CreateGoalPopup(isDialogOpen,
                        dbHelper,
                        onChange = { goalAdded = true },
                        distanceUnit = distanceUnit)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Activity Goals", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(20.dp))

                LaunchedEffect(goalAdded) {
                    if (goalAdded) {
                        GoalList = GoalDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
                        goalAdded = false // Reset the flag
                    }
                }
                //goals

                for (log in GoalList) {
                    // Wrap your GoalsWidget with a Modifier for long press
                    GoalsWidget(log,
                        activityType = activityTypeList.filter { it.id == log.activityTypeId }.first(),
                        distanceUnit = distanceUnit,
                        modifier = Modifier.pointerInput(log) {
                            detectTapGestures(
                                onLongPress = {
                                    selectedGoal = log
                                    showDialog = true
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Delete Goal") },
                        text = { Text("Are you sure you want to delete this goal?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    selectedGoal?.let { goal ->
                                        GoalList.remove(goal)
                                        GoalDAO.delete(dbHelper.writableDatabase, goal)
                                    }
                                }
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }

            // Floating Action Button
            IconButton(
                onClick = { isDialogOpen.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .size(56.dp)
            ) {
                Icon(
                    Icons.Outlined.Add, "Add a goal",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp)
                )
            }
        }
    }


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalPopup(isDialogOpen: MutableState<Boolean>,
                    dbHelper: DBHelper,
                    onChange: () -> Unit,
                    distanceUnit: String){
    var isError by remember { mutableStateOf(false) }


    var activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }
    var activityType: ActivityType? by remember { mutableStateOf(activityTypeList[0]) }
    var activityTypeExpanded by remember { mutableStateOf(false) }


    //database
    var goalMainValue by remember { mutableStateOf(0) }
    var goalHourValue by remember { mutableStateOf(0) }
    var goaldistanceValue by remember { mutableFloatStateOf(0.0f) }
    var hourInput by remember { mutableStateOf("") }
    var mainInput by remember { mutableStateOf("") }
    var distanceInput by remember { mutableStateOf("") }

    var frequency by rememberSaveable { mutableStateOf(GoalFrequency.DAILY) }
    var goalType by rememberSaveable { mutableStateOf(GoalType.DURATION) }

    //popup
    if (isDialogOpen.value) {
        Dialog(onDismissRequest = { isDialogOpen.value = false }) {
            Surface{
                Column(modifier = Modifier.padding(16.dp)) {
                    // Activity Dropdown
                    ExposedDropdownMenuBox(
                        expanded = activityTypeExpanded, onExpandedChange = {activityTypeExpanded = it},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var leadingIcon: (@Composable () -> Unit)? = null
                        if (activityType != null)
                            leadingIcon = @Composable { Icon(Constants.ActivityIcons.values()[activityType!!.iconId].image, activityType!!.name) }

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            value = activityType?.name ?: "", onValueChange = {}, readOnly = true,
                            label = { androidx.compose.material3.Text("Activity Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityTypeExpanded) },
                            leadingIcon = leadingIcon
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
                                            androidx.compose.material3.Text(it.name)
                                        }
                                    },
                                    onClick = { activityType = it; activityTypeExpanded = false },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))


                    GoalsDropdown(options = listOf(GoalFrequency.DAILY.displayName, GoalFrequency.WEEKLY.displayName, GoalFrequency.MONTHLY.displayName),
                        label = "Frequency",
                        selectedOptionText = frequency.displayName,
                        updateSelection = {newSelection -> frequency =GoalFrequency.byName(newSelection)!!},
                        modifier = Modifier.fillMaxWidth(),
                        dropdownWidth = 405.dp
                    )




                    Spacer(modifier = Modifier.height(8.dp))
                    GoalsDropdown(options = listOf(GoalType.STEPS.displayName, GoalType.DISTANCE.displayName, GoalType.SETS.displayName,GoalType.DURATION.displayName),
                        label = "Goal",
                        selectedOptionText = goalType.displayName,
                        updateSelection = {newSelection -> goalType =
                            GoalType.byName(newSelection)!!
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // Goal TextField
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Spacer(modifier = Modifier.width(5.dp))

                        if (goalType == GoalType.DURATION) {
                            TextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = goalHourValue.toString()) },
                                value = hourInput,
                                onValueChange = { hourInput = it},
                                singleLine = true,
                                trailingIcon = { Text(text = "hr") },

                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(5.dp))
                            TextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = goalMainValue.toString()) },
                                value = mainInput,
                                onValueChange = { mainInput = it},
                                singleLine = true,
                                trailingIcon = { Text(text = "min") },

                                modifier = Modifier.weight(1f)
                            )
                        }
                        else if (goalType == GoalType.DISTANCE){

                            TextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = goaldistanceValue.toString()) },
                                value = distanceInput,
                                onValueChange = { distanceInput = it},
                                singleLine = true,
                                trailingIcon = { Text(text = distanceUnit) },

                                modifier = Modifier.weight(1f)
                            )

                        }
                        else{
                            TextField(
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = goalMainValue.toString()) },
                                value = mainInput,
                                onValueChange = { mainInput = it},
                                singleLine = true,
                                trailingIcon = {
                                    Text(
                                        when (goalType) {
                                            GoalType.STEPS -> "steps"
                                            GoalType.SETS -> "sets"
                                            else -> ""

                                        }
                                    )
                                }

                            )
                        }
                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { isDialogOpen.value = false}) {
                            Text("CLOSE")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(onClick = {
                            val g = Goal()
                            goalHourValue = hourInput.toIntOrNull() ?: 0
                            goalMainValue = mainInput.toIntOrNull() ?: 0
                            goaldistanceValue = distanceInput.toFloatOrNull()?: 0f



                            if (goalType == GoalType.DURATION && goalMainValue in 1..60|| goalHourValue in 1..743) {

                                g.activityTypeId = activityType?.id!!
                                g.frequency = frequency
                                g.type = goalType
                                g.value = goalMainValue
                                g.distance = goaldistanceValue
                                g.hours = goalHourValue

                                if (!GoalDAO.insert(dbHelper.writableDatabase, g)) {
                                    Log.d("FIT", "Failed to insert goal into the database")
                                } else {
                                    Log.d("FIT", "Inserted goal into the database!")
                                    isDialogOpen.value = false

                                }
                                onChange()
                            }
                            else if(goalType == GoalType.DISTANCE && goaldistanceValue in 0.1..1000.0) {
                                g.activityTypeId = activityType?.id!!
                                g.frequency = frequency
                                g.type = goalType
                                g.value = goalMainValue
                                if(distanceUnit=="Km"){
                                    goaldistanceValue/= 1.609f

                                }
                                g.distance = goaldistanceValue
                                g.hours = goalHourValue



                                if (!GoalDAO.insert(dbHelper.writableDatabase, g)) {
                                    Log.d("FIT", "Failed to insert goal into the database")
                                } else {
                                    Log.d("FIT", "Inserted goal into the database!")
                                    isDialogOpen.value = false

                                }
                                onChange()
                            }
                            else if(goalType !== GoalType.DURATION && goalMainValue in 1..999999) {
                                g.activityTypeId = activityType?.id!!
                                g.frequency = frequency
                                g.type = goalType
                                g.value = goalMainValue
                                g.distance = goaldistanceValue
                                g.hours = goalHourValue

                                if (!GoalDAO.insert(dbHelper.writableDatabase, g)) {
                                    Log.d("FIT", "Failed to insert goal into the database")
                                } else {
                                    Log.d("FIT", "Inserted goal into the database!")
                                    isDialogOpen.value = false

                                }
                                onChange()
                            }
                            else {
                                isError = true

                            }

                        }) {
                            Text("ADD")

                        }

                    }
                    if (isError) {
                        Text(
                            "Enter a valid Number",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


suspend fun GetGoalsData (
    dataStoreManager: SettingsDataStoreManager,
    onCalorieGoalLoaded: (Int) -> Unit,
    onCalorieChoiceLoaded: (Boolean) -> Unit,
    onDistanceUnitLoaded: (String) -> Unit,
){
    val calorieGoal = dataStoreManager.getIntSetting("CalorieGoalKey", 2000).first()
    onCalorieGoalLoaded(calorieGoal)
    val calorieChoice = dataStoreManager.getSwitchSetting("CalorieKey", true).first()
    onCalorieChoiceLoaded(calorieChoice)
    val distanceUnit = dataStoreManager.getStringSetting("DistanceUnitKey", "Miles").first()
    onDistanceUnitLoaded(distanceUnit)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsDropdown(
    options: List<String>,
    label: String,
    selectedOptionText: String,
    updateSelection: (newSelection: String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp = 500.dp,)

    {
    var expanded by remember { mutableStateOf(false) }


        Box(modifier = Modifier
            .width(dropdownWidth)
            .wrapContentSize(Alignment.TopStart)
        ) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                TextField(
                    modifier = modifier
                        .menuAnchor()
                        .clickable(onClick = { expanded = false }),
                    value = selectedOptionText, onValueChange = {}, label = {
                        androidx.compose.material3.Text(
                            label
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    readOnly = true
                )
                ExposedDropdownMenu(expanded = expanded,
                    onDismissRequest = { expanded = false },
                   ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(

                            text = {
                                Text(
                                    selectionOption,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                updateSelection(selectionOption)
                                expanded = false

                            },

                        )
                    }
                }
            }
    }
}
