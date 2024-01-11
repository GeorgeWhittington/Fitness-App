package com.foxden.fitnessapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.NutritionLog
import com.foxden.fitnessapp.data.NutritionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* Nutrition display component  */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NutritionDisplay(date: LocalDate, logs :List<NutritionLog>, advancedView: Boolean = false) {

    // filter nutrition logs by specified date
    val nutritionLogFiltered = remember {
        logs.filter { it.date!! == date }
    }

    // Calculate sum of each type
    val totalSum = remember { nutritionLogFiltered.sumOf { it.calories } }
    val breakfastSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.BREAKFAST }.sumOf { it.calories } }
    val lunchSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.LUNCH }.sumOf { it.calories } }
    val dinnerSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.DINNER }.sumOf { it.calories } }
    val snacksSum = remember { nutritionLogFiltered.filter { it.type == NutritionType.SNACK }.sumOf { it.calories } }

    val titleStr = when (date) {
        LocalDate.now() -> { "Today" }
        LocalDate.now().minusDays(1) -> { "Yesterday" }
        else -> {
            val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            date.format(pattern)
        }
    }

    Column (
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
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

                Row(modifier = Modifier.fillMaxWidth()){
                    Column (modifier = Modifier.fillMaxWidth(0.5f)){
                        Row {
                            Text(text = titleStr, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column (modifier = Modifier.fillMaxWidth(0.5f)){
                        Row (modifier = Modifier.align(Alignment.End)) {
                            Text(
                                modifier = Modifier.fillMaxWidth(), text = "$totalSum kcal",
                                fontSize = 14.sp, textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                // Render extra details if advanced view
                if (advancedView) {
                    if (nutritionLogFiltered.isEmpty()) {
                        Row {
                            Text(
                                text = "Nothing Logged", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight(700)
                            )
                        }
                    } else {
                        NutritionDetailed(breakfastSum, lunchSum, dinnerSum, snacksSum)
                    }
                }
            }

        }


    }


}

@Composable
fun NutritionDetailed(breakfastSum: Int, lunchSum: Int, dinnerSum: Int, snacksSum: Int) {
    Row {
        Text(
            text = "Breakfast: ${breakfastSum}", fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight(700)
        )
    }

    Row {
        Text(
            text = "Lunch: ${lunchSum}", fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight(700)
        )
    }

    Row {
        Text(
            text = "Dinner: ${dinnerSum}", fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight(700)
        )
    }

    Row {
        Text(
            text = "Snacks: ${snacksSum}", fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight(700)
        )
    }
}