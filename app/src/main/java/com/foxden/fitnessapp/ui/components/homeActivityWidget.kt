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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeWidget(activities:Int,distance:Float,duration:Int,distanceUnit:String){
    val totalDistance: Float = if (distanceUnit=="Km"){
        String.format("%.2f", distance*1.609).toFloat()
    } else{
        String.format("%.2f", distance).toFloat()
    }
    val durationString = String.format("%dh %dm", duration / 3600, (duration % 3600) / 60)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(10.dp)
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
                        Text(text = "$totalDistance $distanceUnit", fontSize = 15.sp)
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
                        Text(text = durationString, fontSize = 15.sp)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
