package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.ui.components.NavBar
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap

@OptIn(MapboxExperimental::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActivityRecordingScreen(navigation: NavController) {
    Scaffold (
        containerColor = Color(134, 187, 216),
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(25.dp)) {
            Text(text = "Record an Activity", fontSize = 20.sp,
                color = Color(11, 45, 61))
            MapboxMap(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                mapInitOptionsFactory = { context ->
                    MapInitOptions(
                        context = context,
                        styleUri = Style.MAPBOX_STREETS,
                        cameraOptions = CameraOptions.Builder()
                            .center(Point.fromLngLat(24.9384, 60.1699))
                            .zoom(9.0)
                            .build()
                    )
                }
            )

            // select activity type

            // add new activity

            // play button
        }
    }
}

@Preview
@Composable
fun PreviewActivityRecordingScreen() {
    val navController = rememberNavController()
    ActivityRecordingScreen(navigation = navController)
}