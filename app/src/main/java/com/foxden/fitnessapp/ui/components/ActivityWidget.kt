package com.foxden.fitnessapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.ui.theme.LightBlue
import com.foxden.fitnessapp.ui.theme.MidBlue
import com.foxden.fitnessapp.ui.theme.Yellow
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class SlideshowImage(val image: Painter, val imageDescription: String?)

@Composable
fun ActivitySlideshow(modifier: Modifier, images: List<SlideshowImage>) {
    var imageIndex by remember { mutableIntStateOf(0) }
    val numImages = images.count() - 1

    Box (modifier = modifier.clickable { if (imageIndex + 1 > numImages) imageIndex = 0 else imageIndex++ }) {
        Image(
            painter = images[imageIndex].image,
            contentDescription = images[imageIndex].imageDescription
        )
        ImageSteppers(
            numImages = numImages + 1, selectedImage = imageIndex,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityWidget(log: ActivityLog, activityType: ActivityType) {

    var startDT: ZonedDateTime? by remember { mutableStateOf(ZonedDateTime.ofInstant(Instant.ofEpochSecond(log.startTime), ZoneId.systemDefault())) }
    var endDT: ZonedDateTime? by remember { mutableStateOf(ZonedDateTime.ofInstant(Instant.ofEpochSecond(log.startTime + log.duration), ZoneId.systemDefault())) }
    var duration: Duration? by remember { mutableStateOf(Duration.between(startDT, endDT)) }

    val images = listOf(
        SlideshowImage(painterResource(R.drawable.hiking_route), "map showing route taken"),
        SlideshowImage(painterResource(R.drawable.hiking_picture), null),
        SlideshowImage(painterResource(R.drawable.hiking_picture), null),
        SlideshowImage(painterResource(R.drawable.hiking_picture), null)
    )

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(MidBlue)
    ) {
        ActivitySlideshow(modifier = Modifier.width(112.dp), images)
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column {
                Row {
                    Icon(
                        Constants.ActivityIcons.values()[activityType.iconId].image, contentDescription = activityType.name,
                        modifier = Modifier.size(20.dp))
                    Text(text = activityType.name, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Column {
                        Text(text = "Distance", fontSize = 12.sp)
                        Text(text = "${log.distance} km", fontSize = 12.sp,
                            color = Yellow,
                            fontWeight = FontWeight(700))
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(text = "Time", fontSize = 12.sp)
                        // TODO: split into hours an minutes
                        Text(text = "${duration?.toMinutes()} min", fontSize = 12.sp,
                            color = Yellow,
                            fontWeight = FontWeight(700))
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(text = "Calories", fontSize = 12.sp)
                        Text(
                            text = "${log.calories}", fontSize = 12.sp,
                            color = Yellow,
                            fontWeight = FontWeight(700))
                    }
                }
            }

            Text(text = "${startDT?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}", fontSize = 10.sp)
        }
    }
}

@Preview
@Composable
fun PreviewActivityWidget() {
    Column (modifier = Modifier.width(310.dp)) {
        ActivityWidget(ActivityLog(), ActivityType())
    }

}