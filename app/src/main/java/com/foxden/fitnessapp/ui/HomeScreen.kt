package com.foxden.fitnessapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalDAO
import com.foxden.fitnessapp.data.GoalFrequency
import com.foxden.fitnessapp.data.GoalType
import com.foxden.fitnessapp.data.NutritionLogDAO
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.ActivityWidget
import com.foxden.fitnessapp.ui.components.HomeGoalWidget
import com.foxden.fitnessapp.ui.components.HomeWidget
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.components.NutritionProgress
import com.foxden.fitnessapp.utils.Evaluate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(navigation: NavController, dbHelper: DBHelper) {
    val dataStoreManager = SettingsDataStoreManager(LocalContext.current)
    val calorieChoice by dataStoreManager.getSettingFlow(Settings.CALORIES_ENABLED).collectAsState(initial = true)
    val calorieGoal by dataStoreManager.getSettingFlow(Settings.CALORIE_GOAL).collectAsState(initial = 2000)
    val character by dataStoreManager.getSettingFlow(Settings.CHARACTER).collectAsState(initial = "")

    // used to load chosen character
    var image = when (character) {
        "Fox" -> { R.drawable.fox_happy }
        "Racoon" -> { R.drawable.racoon }
        "Cat" -> { R.drawable.hendrix_window }
        else -> { 0 }
    }

    //nutrition logs
    val start = LocalDate.now().minusDays(7)
    val end = LocalDate.now()
    val nutritionLogList = remember {
        NutritionLogDAO.fetchRange(dbHelper.writableDatabase, start, end).toMutableStateList()
    }

    //activity logs
    val ActivityLogList = remember {
        ActivityLogDAO.fetchAll(dbHelper.writableDatabase).sortedBy { it.startTime }.toMutableStateList()
    }

    //weekly activity logs
    val currentTime = LocalDateTime.now()

    //goal list
    val GoalList = remember {
        GoalDAO.fetchAll(dbHelper.writableDatabase).sortedBy { it.frequency }.toMutableStateList()
    }

    //activity type
    val activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }

    // weekly stat
    var totalActivities by remember { mutableIntStateOf(0) }
    var totalDuration = 0
    var totalDistance = 0.0f
    for (log in ActivityLogList) {
        totalDuration += log.duration
        totalDistance += log.distance
    }

    // TODO: ALLOW FOR THE TRACKING OF GOALS OTHER THAN TYPE DISTANCE
    val goalDistances: Map<Goal, Double> = GoalList
        .filter { goal -> goal.type == GoalType.DISTANCE }.associateWith { goal ->
            ActivityLogList
                .filter { activity ->
                    GetGoalsActivityLogs(activity, goal, currentTime)
                }
                .sumOf { it.distance.toDouble() }
        }

    // Make evaulation
    val calgoal: Int = if (calorieGoal is Int) calorieGoal as Int else 0
    var evaluation = Evaluate(totalActivities, (calorieChoice == true), calgoal, nutritionLogList, character.toString())
    image = evaluation.image

    Scaffold (
        containerColor = MaterialTheme.colorScheme.secondary,
        bottomBar = { NavBar(navigation = navigation) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.height(200.dp)) {
                    if (image != 0) {
                        Image(
                            painter = painterResource(evaluation.image),
                            contentDescription = stringResource(id = R.string.cat_alt_text)
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Image,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
                Text(text="${evaluation.messagePositive} ${evaluation.approval}")
                Text(text="${evaluation.messageNegative} ${evaluation.approval}")
                Spacer(modifier = Modifier.height(10.dp))

                if (calorieChoice == true) {
                    NutritionProgress(nutritionLogList.filter { it.date == LocalDate.now() }
                        .sumOf { it.calories })
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Recent activity", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (ActivityLogList.isNotEmpty()) {
                    val lastActivity = ActivityLogList.last()
                    ActivityWidget(
                        lastActivity,
                        activityTypeList.filter { it.id == lastActivity.activityTypeId }.first()
                    )
                    totalActivities = ActivityLogList.size
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Please record or manually log your activity", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Statistics", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                HomeWidget(
                    activities = totalActivities,
                    distance = totalDistance,
                    duration = totalDuration
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Goal tracking", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (goalDistances.isNotEmpty()) {
                    val titles = mapOf(
                        GoalFrequency.DAILY to "Daily Goals",
                        GoalFrequency.WEEKLY to "Weekly Goals",
                        GoalFrequency.MONTHLY to "Monthly Goals"
                    )

                    Column {
                        for ((frequency, title) in titles) {
                            Text(text = title, fontSize = 20.sp, modifier = Modifier.padding(vertical = 10.dp))

                            goalDistances.filter { it.key.frequency == frequency }.forEach { (goal, sumDistance) ->
                                HomeGoalWidget(
                                    goal = goal,
                                    sumDistance = sumDistance.toFloat(),
                                    activityTypeList.filter { it.id == goal.activityTypeId }.first()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Add goals to track your progress", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun GetGoalsActivityLogs(activity: ActivityLog, goal: Goal, currentTime: LocalDateTime): Boolean {
    if (activity.activityTypeId != goal.activityTypeId) {
        return false
    }

    // Check frequency
    return when (goal.frequency) {
        GoalFrequency.DAILY -> activity.startTime >= currentTime.minusDays(1).toEpochSecond(ZoneOffset.UTC)
        GoalFrequency.WEEKLY -> activity.startTime >= currentTime.minusWeeks(1).toEpochSecond(ZoneOffset.UTC)
        GoalFrequency.MONTHLY -> activity.startTime >= currentTime.minusMonths(1).toEpochSecond(ZoneOffset.UTC)
    }
}

