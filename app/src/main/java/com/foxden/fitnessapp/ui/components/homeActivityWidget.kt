package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.ui.theme.LightBlue

@Composable
fun HomeWidget(activities:Int,distance:Float,duration:Int,distanceUnit:String){


    var totalDistance: Float

    if (distanceUnit=="Km"){
        totalDistance = String.format("%.2f", distance*1.609).toFloat()
    }else{
        totalDistance = String.format("%.2f", distance).toFloat()
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(LightBlue)
    ) {

        Column (
            verticalArrangement = Arrangement.SpaceBetween,

            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Activities", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$activities", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }

                }
                Column (
                    modifier = Modifier.weight(1f)
                ){
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Total distance", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$totalDistance "+"$distanceUnit", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Total time", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "$duration Min", fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                //Spacer(modifier = Modifier.weight(1f))




            }



        }
    }
}
