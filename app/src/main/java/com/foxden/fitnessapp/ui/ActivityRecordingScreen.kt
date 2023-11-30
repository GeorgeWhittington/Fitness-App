package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foxden.fitnessapp.ui.components.ActivitySelector
import com.foxden.fitnessapp.ui.components.ActivityType
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.ui.theme.LightBlue
import com.foxden.fitnessapp.ui.theme.MidBlue
import com.foxden.fitnessapp.utils.LocationViewModel
import com.foxden.fitnessapp.utils.PermissionEvent
import com.foxden.fitnessapp.utils.ViewState
import com.foxden.fitnessapp.utils.hasLocationPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActivityRecordingScreen(navigation: NavController, locationViewModel: LocationViewModel) {
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION))
    val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()
    var selectedActivity: ActivityType? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(!LocalContext.current.hasLocationPermission()) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Granted) }}
        permissionState.shouldShowRationale -> {
            RationaleAlert(onDismiss = {}) { permissionState.launchMultiplePermissionRequest() }}
        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Revoked) }}
    }

    Scaffold (
        containerColor = LightBlue,
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(25.dp)) {
            Text(text = "Record an Activity", fontSize = 20.sp, color = DarkBlue)

            Spacer(modifier = Modifier.size(15.dp))

            ActivitySelector(selectedActivity, setSelectedActivity = { selectedActivity = it })

            Spacer(modifier = Modifier.height(20.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(10.dp)
                    .clickable {
                        // TODO: Add new activity type form
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Add, null)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add new activity")
            }

            Spacer(modifier = Modifier.height(20.dp))

            with (viewState) {
                when (this) {
                    ViewState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    ViewState.RevokedPermissions -> {
                        RevokedPermsMap()
                    }

                    is ViewState.Success -> {
                        val currentLocation = this.location
                        val cameraPositionState = rememberCameraPositionState()

                        LaunchedEffect(key1 = currentLocation) {
                            if (currentLocation != null)
                                cameraPositionState.centerOnLocation(currentLocation)
                        }
                        Map(cameraPositionState)
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .background(MidBlue, CircleShape)
                        .size(65.dp)
                ) {
                    Icon(
                        Icons.Filled.PlayArrow, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(65.dp))
                }
            }

            // To account for navbar
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun Map(cameraPositionState: CameraPositionState) {
    GoogleMap (
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp)),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL
        )
    )
}

@Composable
fun RevokedPermsMap() {
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* TODO: probably specify the activity type currently selected, and change behaviour based
        * on if that activity actually *needs* gps */
        Text("Location permissions are required to track a jog")
        Button(
            onClick = { startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null) },
            enabled = !context.hasLocationPermission()
        ) {
            if (context.hasLocationPermission()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = Color.White
                )
            } else {
                Text("Settings")
            }
        }
    }
}

@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface (
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column (modifier = Modifier.padding(16.dp)) {
                Text(text = "Location permissions are required to record an activity!")
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = { onConfirm(); onDismiss() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location, 15f
    ),
    durationMs = 1500
)