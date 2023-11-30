package com.foxden.fitnessapp.ui.components

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
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
import com.foxden.fitnessapp.ui.theme.MidBlue

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
        // Only render steppers if there are multiple images
        if (numImages != 0) {
            Row(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
            ) {
                images.forEachIndexed { index, _ ->
                    if (index == imageIndex) {
                        Icon(
                            painterResource(R.drawable.filled_circle), contentDescription = null,
                            modifier = Modifier.size(7.dp)
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.stroked_circle), contentDescription = null,
                            modifier = Modifier.size(7.dp)
                        )
                    }
                    // Don't add a spacer if this is the last stepper!
                    if (index != numImages) {
                        Spacer(modifier = Modifier.size(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityWidget() {
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
            .background(Color.White)
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
                        Icons.Outlined.DirectionsWalk, contentDescription = "Person Walking",
                        modifier = Modifier.size(20.dp))
                    Text(text = "Afternoon Hike", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Column {
                        Text(text = "Distance", fontSize = 12.sp)
                        Text(text = "8.01km", fontSize = 12.sp,
                            color = MidBlue,
                            fontWeight = FontWeight(700))
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(text = "Time", fontSize = 12.sp)
                        Text(text = "1h 55m", fontSize = 12.sp,
                            color = MidBlue,
                            fontWeight = FontWeight(700))
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(text = "Calories", fontSize = 12.sp)
                        Text(
                            text = "1200", fontSize = 12.sp,
                            color = MidBlue,
                            fontWeight = FontWeight(700))
                    }
                }
            }
            Text(text = "Today at 11:45 AM", fontSize = 10.sp)
        }
    }
}

@Preview
@Composable
fun PreviewActivityWidget() {
    Column (modifier = Modifier.width(310.dp)) {
        ActivityWidget()
    }

}