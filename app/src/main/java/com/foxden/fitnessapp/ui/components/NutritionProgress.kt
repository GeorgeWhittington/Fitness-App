package com.foxden.fitnessapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.GetGoalsData

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NutritionProgress(numCalories: Int) {

    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)

    var calorieTarget by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        GetGoalsData(
            dataStoreManager,
            onCalorieGoalLoaded = { loadedCalorieGoal ->
                calorieTarget = loadedCalorieGoal
            },
            onCalorieChoiceLoaded = { loadedCalorieChoice ->
            },
            onDistanceUnitLoaded = { loadedDistanceUnit ->
            },
        )
    }

    val progress = (numCalories / calorieTarget).toFloat() //: Float = calorieTarget / numCalories


    val containerModifier = Modifier
        .fillMaxWidth()
        //.height(279.dp)
        //.focusable()
        .clip(RoundedCornerShape(20.dp))


    Column (
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(modifier = Modifier) {

                    Column(modifier = Modifier
                        .fillMaxWidth()){

                        Row(modifier = Modifier.align(Alignment.CenterHorizontally)){
                            Text("$numCalories/$calorieTarget kcal")
                        }
                        Row {
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0, 0, 255),
                            )
                        }
                    }
                }
            }
        }
    }

}