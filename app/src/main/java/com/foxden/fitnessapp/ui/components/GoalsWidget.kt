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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.Goal
import com.foxden.fitnessapp.data.GoalType
import com.foxden.fitnessapp.ui.theme.MidBlue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalsWidget(log: Goal,
                activityType: ActivityType,
                distanceUnit: String,
                modifier: Modifier = Modifier) {

    var goalDistance: Float

    if (distanceUnit=="Km"){
        goalDistance = String.format("%.2f", log.distance*1.609).toFloat()
    }else{
        goalDistance = String.format("%.2f", log.distance).toFloat()
    }

    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(MidBlue)
    ) {

        Column (
            verticalArrangement = Arrangement.SpaceBetween,

            modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row {
                Column (
                    verticalArrangement = Arrangement.Center
                ){

                    Row {

                        Icon(
                            Constants.ActivityIcons.values()[activityType.iconId].image, contentDescription = activityType.name,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    //Spacer(modifier = Modifier.weight(1f))
                    Row {

                        Text(text = activityType.name, fontSize = 16.sp)
                    }


                }

                Spacer(modifier = Modifier.weight(1f))


                if(log.type == GoalType.DURATION){
                    if(log.hours >0){
                        Column (

                        ){
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "${log.hours}",
                                    fontSize = 50.sp,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(
                                    text = "Hr",
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .alignByBaseline()
                                        .padding(start = 4.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${log.value}",
                                    fontSize = 50.sp,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(
                                    text = "Min",
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .alignByBaseline()
                                        .padding(start = 4.dp)
                                )
                            }



                        }
                    }
                    else{
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {

                                Text(
                                    text = "${log.value}",
                                    fontSize = 50.sp,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Text(
                                    text = "Min",
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .alignByBaseline()
                                        .padding(start = 4.dp)
                                )
                            }



                        }
                    }

                }else if(log.type == GoalType.DISTANCE){


                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Text(
                                text = "$goalDistance",
                                fontSize = 50.sp,
                                modifier = Modifier.alignByBaseline()
                            )

                            Text(
                                text = distanceUnit,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .alignByBaseline()
                                    .padding(start = 6.dp)
                            )
                        }

                    }
                }

                else{

                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Text(
                                text = "${log.value}",
                                fontSize = 50.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                            Text(
                                text = "${log.type}",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .alignByBaseline()
                                    .padding(start = 6.dp)
                            )
                        }

                    }
                }


                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Modifier.weight(1f)
                    Text(text = "${log.frequency} ", fontSize = 12.sp,

                        fontWeight = FontWeight(700)
                    )
                }




            }



        }
    }
}