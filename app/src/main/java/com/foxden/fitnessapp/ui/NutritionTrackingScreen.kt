package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionLogDAO
import com.foxden.fitnessapp.data.NutritionType
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.components.NutritionDisplay
import com.foxden.fitnessapp.ui.components.NutritionProgress
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.ui.theme.LightBlue
import com.foxden.fitnessapp.ui.theme.MidBlue
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NutritionTrackingScreen(navigation: NavController, dbHelper: DBHelper) {

    val focusManager = LocalFocusManager.current

    // Get last 7 days
    val start = LocalDate.now().minusDays(7)
    val end = LocalDate.now()
    var nutritionLogList = remember {
        NutritionLogDAO.fetchRange(dbHelper.writableDatabase, start, end).toMutableStateList()
    }

    Scaffold (
        containerColor = LightBlue,
        bottomBar = { NavBar(navigation = navigation) },
        modifier = Modifier
            .focusable()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.moveFocus(FocusDirection.Exit) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 25.dp)

            ) {

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nutrition Tracking", fontSize = 20.sp,
                        color = DarkBlue
                    )
                    Row {
                        IconButton(
                            onClick = { navigation.navigate(Routes.NUTRITION_LOG_MEAL_SCREEN) },
                            modifier = Modifier.offset(x = 9.dp, y = (-9).dp)
                        ) {
                            androidx.compose.material.Icon(
                                Icons.Outlined.Add, contentDescription = "Log Meal",
                                tint = MidBlue, modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = { navigation.navigate(Routes.NUTRITION_ADD_PRESET_SCREEN) },
                            modifier = Modifier.offset(x = 9.dp, y = (-9).dp)
                        ) {
                            androidx.compose.material.Icon(
                                Icons.Outlined.Edit, contentDescription = "Add Preset",
                                tint = MidBlue, modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Spacer(modifier = Modifier.size(15.dp))

                        // Todays values
                        NutritionProgress(nutritionLogList.filter { it.date == LocalDate.now() }.sumOf { it.calories })
                        Spacer(modifier = Modifier.size(4.dp))
                        NutritionDisplay(LocalDate.now(), nutritionLogList, true)
                        Spacer(modifier = Modifier.size(4.dp))

                        // Yesterdays values
                        NutritionDisplay(LocalDate.now().minusDays(1), nutritionLogList, true)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Previous")
                        Spacer(modifier = Modifier.size(4.dp))

                        // Values for the past
                        for (i in 2..6) {
                            NutritionDisplay(LocalDate.now().minusDays(i.toLong()), nutritionLogList)
                            Spacer(modifier = Modifier.size(4.dp))
                        }
                    }
                }




            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun AddNutritionLog(dbHelper: DBHelper)
{
    var l = NutritionLog()
    l.type = NutritionType.SNACK
    l.date = LocalDate.now()
    l.calories = 6969

    if (!NutritionLogDAO.insert(db = dbHelper.writableDatabase, l)) {
        Log.d("FIT", "Failed to insert into database.")
    } else {
        Log.d("FIT", "Inserted Successfully!")
    }

}