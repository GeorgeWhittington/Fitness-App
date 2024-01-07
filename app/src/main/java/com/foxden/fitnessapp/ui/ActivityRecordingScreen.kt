package com.foxden.fitnessapp.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.ui.components.ActivitySelector
import com.foxden.fitnessapp.ui.components.AddActivityTypeDialog
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.components.AddActivityTypeErrorDialog
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
@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ActivityRecordingScreen(navigation: NavController, locationViewModel: LocationViewModel, dbHelper: DBHelper) {
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION))
    val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()
    var selectedActivity: ActivityType? by remember { mutableStateOf(null) }
    val focusManager = LocalFocusManager.current

    val activityList = remember { mutableStateListOf<ActivityType>() }
    activityList.clear()
    for (a in ActivityTypeDAO.fetchAll(dbHelper.readableDatabase)) {
        activityList.add(a)
    }

    // TODO: Initial value should be the most commonly used activity type, read from db
    LaunchedEffect(null) {
        selectedActivity = activityList[0]
    }

    // --- location permission logic ---
    LaunchedEffect(!LocalContext.current.hasLocationPermission() && selectedActivity?.gpsEnabled ?: true) {
        if (selectedActivity?.gpsEnabled != false)
            permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Granted) }}
        permissionState.shouldShowRationale -> {
            RationaleAlert(selectedActivity, onDismiss = {}) { permissionState.launchMultiplePermissionRequest() }}
        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Revoked) }}
    }

    // --- add activity type dialog ---
    var showActivityDialog by remember { mutableStateOf(false) }
    var showActivityDialogError by remember { mutableStateOf(false) }
    var activityDialogErrorMessage by remember { mutableStateOf("") }

    if (showActivityDialog) {
        AddActivityTypeDialog(
            onDismiss = { showActivityDialog = false },
            onError = {
                showActivityDialog = false
                activityDialogErrorMessage = it
                showActivityDialogError = true
            },
            dbHelper)
    }

    if (showActivityDialogError) {
        AddActivityTypeErrorDialog(
            errorMessage = activityDialogErrorMessage,
            onDismiss = { showActivityDialogError = false })
    }

    // --- content ---
    Scaffold (
        containerColor = MaterialTheme.colorScheme.primary,
        bottomBar = { NavBar(navigation = navigation) },
        modifier = Modifier
            .focusable()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.moveFocus(FocusDirection.Exit) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 25.dp)
            ) {
                Text(text = "Record an Activity", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)

                Spacer(modifier = Modifier.size(15.dp))

                ActivitySelector(selectedActivity, setSelectedActivity = { selectedActivity = it }, activityList)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showActivityDialog = true },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Add new activity", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedActivity?.gpsEnabled != false) {
                    with(viewState) {
                        when (this) {
                            // TODO: the loading state is only selected if the user has provided
                            // permissions previously. If they have only just given permissions
                            // the state goes from RevokedPermissions -> Success instead of
                            // correctly doing RevokedPermissions -> Loading -> Success
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
                                RevokedPermsMap(selectedActivity)
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
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { /* TODO - open correct activity recording screen for whichever type is selected */ },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                            .size(65.dp)
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(65.dp)
                        )
                    }
                }
            }
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
fun RevokedPermsMap(selectedActivity: ActivityType?) {
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (selectedActivity != null) {
            Text(
                "Location permissions are required to track ${selectedActivity.name}",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Text(
                text = "Location permissions are required to track some activities",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Button(
            onClick = { startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null) },
            enabled = !context.hasLocationPermission()
        ) {
            if (context.hasLocationPermission()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Text("Settings", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun RationaleAlert(selectedActivity: ActivityType?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        title = { Text("Please enable location permissions") },
        text = {
            if (selectedActivity != null)
                Text("Location permissions are required to track ${selectedActivity.name}")
            else
                Text("Location permissions are required to track some activities")
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text("OK")
            }
        }
    )
}

private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location, 15f
    ),
    durationMs = 1500
)