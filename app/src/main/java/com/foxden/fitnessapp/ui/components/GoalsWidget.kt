package com.foxden.fitnessapp.ui.components

import android.os.Build
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalType

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalsWidget(log: Goal, activityType: ActivityType) {



    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(Color.Gray)
    ) {

        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row {
                Spacer(modifier = Modifier.size(5.dp))
                Column {

                    Icon(
                        Constants.ActivityIcons.values()[activityType.iconId].image, contentDescription = activityType.name,
                        modifier = Modifier.size(50.dp))
                    Text(text = activityType.name, fontSize = 16.sp)

                }

                Spacer(modifier = Modifier.size(50.dp))
                Row {





                    if(log.hours >0){
                        Column {

                            Text(
                                text = "${log.hours}", fontSize = 50.sp,
                                fontWeight = FontWeight(700)
                            )
                            Text(text = "hours", fontSize = 16.sp)
                        }
                    }
                    else{}

                    Spacer(modifier = Modifier.size(50.dp))
                    Column {
                        Text(
                            text = "${log.value}", fontSize = 50.sp,

                            fontWeight = FontWeight(700)
                        )
                        if(log.type ==GoalType.DISTANCE){
                            Text(text = "Km", fontSize = 16.sp)
                        }
                        else{
                            Text(text = "Min", fontSize = 16.sp)
                        }

                    }
                    Column {

                        Text(text = "${log.frequency} ", fontSize = 12.sp,

                            fontWeight = FontWeight(700)
                        )
                    }

                }
            }


        }
    }
}