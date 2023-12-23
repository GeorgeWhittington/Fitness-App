package com.foxden.fitnessapp.ui.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.ui.theme.MidBlue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalsWidget(log: Goal, activityType: Goal) {
    Log.d("TAG", "TEST")


    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(Color.White)
    ) {

        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column {

                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Column {
                        Text(text = "Distance", fontSize = 12.sp)
                        Text(text = "${log.frequency} km", fontSize = 12.sp,
                            color = MidBlue,
                            fontWeight = FontWeight(700)
                        )
                    }

                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(text = "Calories", fontSize = 12.sp)
                        Text(
                            text = "${log.type}", fontSize = 12.sp,
                            color = MidBlue,
                            fontWeight = FontWeight(700)
                        )
                    }
                }
            }


        }
    }
}