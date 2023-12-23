package com.foxden.fitnessapp.ui
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalDAO
import com.foxden.fitnessapp.data.GoalFrequency
import com.foxden.fitnessapp.data.GoalType
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.theme.MidBlue
import kotlinx.coroutines.flow.first


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
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
    var currentCalorieGoal by rememberSaveable { mutableFloatStateOf(0f) }

    //get goals from database
    var GoalList = remember {
        GoalDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackIcon { navigation.navigate(Routes.SETTINGS_SCREEN) } },
                backgroundColor = MidBlue,
                modifier = Modifier.height(56.dp),
                actions = {

                    SaveOption(isModified = isModified) {
                        triggerSave.value = true
                        isModified = false
                    }
                    LaunchedEffect(triggerSave.value) {


                        if (triggerSave.value) {

                            dataStoreManager.saveFloatSetting("CalorieGoalKey", currentCalorieGoal)

                            triggerSave.value = false
                        }
                    }}
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                PageName(text = "Goals")
            }

        },


        bottomBar = { NavBar(navigation = navigation) }
    ) { innerPadding ->
        //get data from datastore
        LaunchedEffect(Unit) {
            GetGoalsData(dataStoreManager,
                onCalorieGoalLoaded = { loadedCalorieGoal ->
                    currentCalorieGoal = loadedCalorieGoal
                },

            )
        }

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



                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Daily calorie goal", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(20.dp))


                FloatInputField(
                    icon = Icons.Outlined.Restaurant,
                    placeholder = "Daily calorie goal",
                    value = currentCalorieGoal,
                    onValueChange = { newValue -> currentCalorieGoal = newValue },
                    unit = "kcal",
                    min =  500f,
                    max = 4000f,
                    onChange = { isModified = true },
                    keyboardType = KeyboardType.Number
                )

                if (isDialogOpen.value) {
                    CreateGoalPopup(isDialogOpen,dbHelper)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Activities", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                }

                //goals
                for (log in GoalList) {
                    Log.d("TAG", "TEST1")
                    //GoalsWidget(log, GoalList.filter{ it.id ==  log.id}.first())
                    Spacer(modifier = Modifier.size(10.dp))
                }


            }



            // Floating Action Button
            IconButton(
                onClick = { isDialogOpen.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(MidBlue, CircleShape)
                    .size(56.dp)
            ) {
                Icon(
                    Icons.Outlined.Add, "Add a goal",
                    tint = Color.White, modifier = Modifier.size(24.dp)
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalPopup(isDialogOpen: MutableState<Boolean>, dbHelper: DBHelper) {

    var activityType: ActivityType? by remember { mutableStateOf(null) }
    var activityTypeExpanded by remember { mutableStateOf(false) }

    var activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }
    var isFrequencyEnabled by remember { mutableStateOf(false) }
    var goalValue by remember { mutableStateOf(1000) }

    var frequency by rememberSaveable { mutableStateOf(GoalFrequency.DAILY) }
    var goalType by rememberSaveable { mutableStateOf(GoalType.STEPS) }

    //popup
    if (isDialogOpen.value) {
        Dialog(onDismissRequest = { isDialogOpen.value = false }) {
            Surface {
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GoalsDropdown(options = listOf(GoalFrequency.DAILY.displayName, GoalFrequency.WEEKLY.displayName, GoalFrequency.MONTHLY.displayName),
                            label = "Frequency",
                            selectedOptionText = frequency.displayName,
                            updateSelection = {newSelection -> frequency =
                                GoalFrequency.byName(newSelection)!!
                            },
                            dropdownWidth = 200.dp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = isFrequencyEnabled,
                            onCheckedChange = { isFrequencyEnabled = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Goal TextField
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GoalsDropdown(options = listOf(GoalType.STEPS.displayName, GoalType.DISTANCE.displayName, GoalType.SETS.displayName),
                            label = "Goal",
                            selectedOptionText = goalType.displayName,
                            updateSelection = {newSelection -> goalType =
                                GoalType.byName(newSelection)!!
                            },
                            dropdownWidth = 150.dp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        TextField(
                            value = goalValue.toString(),
                            onValueChange = { goalValue = it.toIntOrNull()?:0 },
                            singleLine = true

                        )
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

                            g.activityTypeId = activityType?.id!!
                            g.frequency = frequency
                            g.type = goalType
                            g.value = goalValue

                            if (!GoalDAO.insert(dbHelper.writableDatabase, g)) {
                                Log.d("FIT", "Failed to insert goal into the database")
                            } else {
                                Log.d("FIT", "Inserted goal into the database!")
                                isDialogOpen.value = false
                            }

                        }) {
                            Text("ADD")
                        }
                    }
                }
            }
        }
    }
}

suspend fun GetGoalsData (
    dataStoreManager: SettingsDataStoreManager,
    onCalorieGoalLoaded: (Float) -> Unit
){

    val calorieGoal = dataStoreManager.getFloatSetting("CalorieGoalKey", 0f).first()
    onCalorieGoalLoaded(calorieGoal)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsDropdown(
    options: List<String>,
    label: String,
    selectedOptionText: String,
    updateSelection: (newSelection: String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp = 200.dp,)

    {
    var expanded by remember { mutableStateOf(false) }


        Box(modifier = Modifier
            .width(dropdownWidth)
            .wrapContentSize(Alignment.TopStart)
        ) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                TextField(
                    modifier = Modifier
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
                    modifier = Modifier
                        .width(dropdownWidth)) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(

                            text = {
                                androidx.compose.material3.Text(
                                    selectionOption,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                updateSelection(selectionOption)
                                expanded = false

                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
    }
}
