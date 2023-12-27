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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionType
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.ui.theme.MidBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NutritionDisplay(date: LocalDate, logs :List<NutritionLog>) {

    val containerModifier = Modifier
        .fillMaxWidth()
        //.height(279.dp)
        //.focusable()
        .clip(RoundedCornerShape(20.dp))

    val nutritionLogFiltered = remember {
        logs.filter { it.date!! == date }
    }

    val breakfastSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.BREAKFAST }.sumOf { it.calories } }
    val lunchSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.LUNCH }.sumOf { it.calories } }
    val dinnerSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.DINNER }.sumOf { it.calories } }
    val snacksSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.SNACK }.sumOf { it.calories } }

    var titleStr = ""

    if (date == LocalDate.now()) {
        titleStr = "Today"
    } else if (date == LocalDate.now().minusDays(1)) {
        titleStr = "Yesterday"
    } else {
        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        titleStr = date.format(pattern)
    }

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
                    Text(text = "$titleStr (${nutritionLogFiltered.size})", fontSize = 14.sp, color = DarkBlue)
                }

                if (nutritionLogFiltered.isEmpty()) {
                    Row {
                        Text(text = "Nothing Logged", fontSize = 12.sp, color = MidBlue, fontWeight = FontWeight(700))
                    }
                } else {
                    NutritionDetailed(breakfastSum, lunchSum, dinnerSum, snacksSum)
                }

            }

        }


    }


}

@Composable
fun NutritionDetailed(breakfastSum: Int, lunchSum: Int, dinnerSum: Int, snacksSum: Int) {
    Row {
        Text(text = "Breakfast: ${breakfastSum}", fontSize = 12.sp, color = MidBlue, fontWeight = FontWeight(700))
    }

    Row {
        Text(text = "Lunch: ${lunchSum}", fontSize = 12.sp, color = MidBlue, fontWeight = FontWeight(700))
    }

    Row {
        Text(text = "Dinner: ${dinnerSum}", fontSize = 12.sp, color = MidBlue, fontWeight = FontWeight(700))
    }

    Row {
        Text(text = "Snacks: ${snacksSum}", fontSize = 12.sp, color = MidBlue, fontWeight = FontWeight(700))
    }
}