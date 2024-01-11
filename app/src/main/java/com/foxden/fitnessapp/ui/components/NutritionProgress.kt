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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager

/*  NutritionProgress component
    Shows a nice progress bar showing the calories logged / calories remaining 
*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NutritionProgress(numCalories: Int) {

    // Get calorie target from data store
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
    val calorieTarget by dataStoreManager.getSettingFlow(Settings.CALORIE_GOAL).collectAsState(initial = 0)

    var progress by remember { mutableStateOf(0.0f) }

    // calculate progress
    if (calorieTarget != 0) {
        progress = numCalories.toFloat() / calorieTarget.toFloat()
    }

    val containerModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))

    Column (
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
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
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                }
            }
        }
    }

}

private fun Any?.toFloat(): Float {
    if (this is Int) {
        return this.toFloat()
    }
    return 0.0f
}