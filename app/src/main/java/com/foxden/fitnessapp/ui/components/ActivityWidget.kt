package com.foxden.fitnessapp.ui.components

import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.utils.formatDistance
import io.jenetics.jpx.GPX
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

class SlideshowImage(val image: ImageBitmap, val imageDescription: String?)
class SlideshowImage2(val url: String, val imageDescription: String?)

@Composable
fun ActivitySlideshow(modifier: Modifier, images: MutableList<Any>) {
    var imageIndex by remember { mutableIntStateOf(0) }
    val numImages = images.count() - 1

    if (images.isNotEmpty()) {
        Box(modifier = modifier.clickable {
            if (imageIndex + 1 > numImages) imageIndex = 0 else imageIndex++
        }) {
            val slideshowImage = images[imageIndex]
            if (slideshowImage is SlideshowImage) {
                Image(
                    bitmap = slideshowImage.image,
                    contentDescription = slideshowImage.imageDescription,
                    contentScale = ContentScale.Crop
                )
            } else if (slideshowImage is SlideshowImage2) {
                Image(
                    painter = rememberAsyncImagePainter(slideshowImage.url) ,
                    contentDescription = slideshowImage.imageDescription,
                    contentScale = ContentScale.Crop
                )
            }

            ImageSteppers(
                numImages = numImages + 1, selectedImage = imageIndex,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ActivityWidget(log: ActivityLog, activityType: ActivityType, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
    val distanceUnit by dataStoreManager.getSettingFlow(Settings.DISTANCE_UNIT)
        .collectAsState(initial = "")
    val caloriesEnabled by dataStoreManager.getSettingFlow(Settings.CALORIES_ENABLED)
        .collectAsState(initial = true)
    val darkMode = MaterialTheme.colorScheme.secondary == DarkBlue

    val startDT: ZonedDateTime? =
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(log.startTime), ZoneId.systemDefault())
    val endDT: ZonedDateTime? = ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(log.startTime + log.duration),
        ZoneId.systemDefault()
    )
    val duration: Duration? = Duration.between(startDT, endDT)
    val totalSeconds = duration?.seconds
    val durationString = if (totalSeconds != null)
        String.format("%dh %dm", totalSeconds / 3600, (totalSeconds % 3600) / 60)
    else ""
    val activityDistance: Float = formatDistance(log.distance, distanceUnit)

    val images = remember { mutableStateListOf<Any>() }
    LaunchedEffect(Unit) {
        val filesDir = context.filesDir.path
        if (log.gpx.isNotEmpty()) {
            var apiUrl = "https://maps.googleapis.com/maps/api/staticmap?size=1000x1000"
            apiUrl += "&key=AIzaSyBdxPDBDXEgyBCkiFYTztLMQNJnUYDzsgI"
            if (darkMode)
                apiUrl += "&map_id=80a2ba4bcd1dc68f"

            var pathStr = "&path=color:red|"
            val gpx = GPX.read(Path("${filesDir}/gpx/${log.gpx}"))
            gpx.tracks().flatMap { it.segments() }.flatMap { it.points() }.forEach {
                pathStr += "${it.latitude},${it.longitude}|"
            }
            pathStr = pathStr.dropLast(1)
            apiUrl += pathStr
            images.add(SlideshowImage2(apiUrl, "Route map"))
        }
        for (path: String in log.images) {
            val bitmap = BitmapDrawable(context.resources, "${filesDir}/${path}").bitmap
            if (bitmap != null)
                images.add(SlideshowImage(bitmap.asImageBitmap(), null))
        }
    }

    Row (
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
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
                    Text(text = log.title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    if (activityType.gpsEnabled) {
                        Column {
                            Text(
                                text = "Distance",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$activityDistance $distanceUnit", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight(700)
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                    Column {
                        Text(text = "Time", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(text = durationString, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight(700))
                    }
                    if (caloriesEnabled == true) {
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text(
                                text = "Calories",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = log.calories.toString(), fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight(700)
                            )
                        }
                    }
                }
            }

            Text(
                text = "${startDT?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}", fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}