package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.utils.LocationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

//@OptIn(MapboxExperimental::class)
//@Composable
//fun AddMarker(point: Point) {
//    CircleAnnotation(
//        point = point,
//        circleRadius = 8.0,
//        circleColorString = "#EE4E8B",
//        circleStrokeWidth = 2.0,
//        circleStrokeColorString = "#FFFFFF"
//    )
//}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActivityRecordingScreen(navigation: NavController, locationService: LocationService) {
    val coroutineScope = rememberCoroutineScope()
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
//    var currentLocation: Point? by remember { mutableStateOf(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.35, 103.87), 10f)
    }

//    val locationCallback: LocationCallback = object : LocationCallback() {
//        override fun onLocationResult(result: LocationResult) {
//            for (location in result.locations) {
//                Log.d("FIT", "${location.time}: lat ${location.latitude}, long ${location.longitude}")
//                currentLocation = Point.fromLngLat(location.latitude, location.longitude)
//
//                mapViewportState.flyTo(
//                    CameraOptions.Builder()
//                        .center(currentLocation)
//                        .build()
//                )
//            }
//        }
//    }

    // this should hopefully only run once?
//    if (permissionState.status.isGranted && currentLocation == null) {
//        LaunchedEffect(currentLocation) {
//            coroutineScope.launch {
//                currentLocation = locationService.getLastLocation()
//                locationService.startLocationUpdates(locationCallback)
//
//                mapViewportState.flyTo(
//                    CameraOptions.Builder()
//                        .center(currentLocation)
//                        .zoom(12.0)
//                        .build(),
//                    MapAnimationOptions.Builder().duration(1500L).build()
//                )
//            }
//        }
//    }

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
            Spacer(modifier = Modifier.size(15.dp))
            GoogleMap (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp)),
                cameraPositionState = cameraPositionState
            ) {
                // add marker showing user position
            }

            // select activity type

            // add new activity

            // play button
        }
    }
}