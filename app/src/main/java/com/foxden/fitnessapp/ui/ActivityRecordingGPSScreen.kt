package com.foxden.fitnessapp.ui

import android.Manifest
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.Settings
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.Steppers
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.utils.LocationViewModel
import com.foxden.fitnessapp.utils.PermissionEvent
import com.foxden.fitnessapp.utils.ViewState
import com.foxden.fitnessapp.utils.centerOnLocation
import com.foxden.fitnessapp.utils.hasLocationPermission
import com.foxden.fitnessapp.utils.saveGPXToInternalStorage
import com.foxden.fitnessapp.utils.saveImageToInternalStorage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import io.jenetics.jpx.GPX
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ActivityRecordingGPSScreen(activityTypeId: Int, navigation: NavController, dbHelper: DBHelper, locationViewModel: LocationViewModel) {
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
    val caloriesEnabled by dataStoreManager.getSettingFlow(Settings.CALORIES_ENABLED).collectAsState(initial = true)
    val calorieUnit by dataStoreManager.getSettingFlow(Settings.CALORIE_UNIT).collectAsState(initial = "")
    val distanceUnit by dataStoreManager.getSettingFlow(Settings.DISTANCE_UNIT).collectAsState(initial = "")
    val distanceUnitCorrected = if (distanceUnit == "Miles") "Mile" else distanceUnit

    if (!context.hasLocationPermission()) {
        // this shouldn't happen, but if it does just exit immediately
        navigation.popBackStack()
    }

    val activityList = remember { ActivityTypeDAO.fetchAll(dbHelper.readableDatabase).toMutableStateList() }
    val selectedActivity: ActivityType? = try {
        activityList.first { log -> log.id == activityTypeId }
    } catch (e: NoSuchElementException) {
        // no or invalid activity type passed, exit immediately
        navigation.popBackStack()
        null
    }

    val startTime: ZonedDateTime by remember { mutableStateOf(ZonedDateTime.now()) }
    var timerPaused by remember { mutableStateOf(true) }
    var ticks by remember { mutableStateOf(0) }
    val locations = remember { mutableStateListOf<LatLng>() }
    var totalDistance by remember { mutableStateOf(0.0f) } // in meters
    val timeString = String.format("%01d:%02d:%02d", ticks / 3600, (ticks % 3600) / 60, ticks % 60)
    val distanceString = String.format("%.2f", if (distanceUnit == "Meters") totalDistance * 0.00062137f else totalDistance / 1000.0f)
    val calorieString = String.format("%d", ticks / 60 * 65 * 7)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            if (!timerPaused)
                ticks++
        }
    }
    LaunchedEffect(Unit) {
        var previousLocationIndex: Int? = null
        while (true) {
            delay(1.seconds)

            val currentLocation = locations.lastOrNull()

            if (previousLocationIndex == null || currentLocation == null) {
                previousLocationIndex = locations.lastIndex
                continue
            }

            // if the user pauses, moves a significant distance and then unpauses
            // that distance should *not* be counted. This should invalidate that
            // sort of data
            if (timerPaused) {
                previousLocationIndex = null
                continue
            }

            val previousLocation = locations[previousLocationIndex]
            val results = FloatArray(1)
            Location.distanceBetween(
                previousLocation.latitude, previousLocation.longitude,
                currentLocation.latitude, currentLocation.longitude,
                results
            )

            // check if distance is greater than a 2 meters, if so add to distance and update prev location
            // otherwise leave prev location as it is, and wait until the current location is far enough away
            // to be worth storing. This should stop the distance from rocketing when someone is at a standstill
            if (results[0] >= 2.0f) {
                totalDistance += results[0]
                previousLocationIndex = locations.lastIndex

                // TODO: Keep track of split pace (speed of last kilometer/mile)
            }
        }
    }

    val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION))
    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Granted) }}
        !permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Revoked) }}
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val rectangleContainerModifier = Modifier
        .fillMaxWidth()
        .height(116.dp)
        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))

    val totalCards = if (caloriesEnabled == true) 3 else 2
    var selectedCard by remember { mutableStateOf(0) }
    val incrementSelectedCard = {
        val newValue = selectedCard + 1
        selectedCard = if (newValue >= totalCards) 0 else newValue
    }

    var mapMaximised by remember { mutableStateOf(false) }

    var showFinalisationPopUp by remember { mutableStateOf(false) }
    fun addActivity(title: String?, notes: String?, imageURIs: List<Uri>?) {
        if (title == null || notes == null || imageURIs == null || selectedActivity == null) {
            navigation.popBackStack()
            return
        }

        val savedUris = mutableListOf<String>()
        imageURIs.forEach {
            savedUris.add(saveImageToInternalStorage(context, it))
        }

        val gpx = GPX.builder()
            .addTrack { track -> track
                .addSegment { segment ->
                    locations.forEach { latLng ->
                        segment.addPoint { p -> p.lat(latLng.latitude).lon(latLng.longitude)}
                    }
                }
            }
            .build()

        ActivityLogDAO.insert(dbHelper.writableDatabase, ActivityLog(
            title = title,
            activityTypeId = selectedActivity.id,
            notes = notes,
            startTime = startTime.toEpochSecond(),
            duration = ticks,
            // convert distance from meters to miles in order to store it
            distance = totalDistance * 0.00062137f,
            // TODO: make this actually accurate, since relative effort can actually be calculated here
            calories = ticks / 60 * 65 * 7,
            images = savedUris,
            gpx = saveGPXToInternalStorage(context, gpx)
        ))
        navigation.popBackStack()
    }

    if (showFinalisationPopUp) {
        FinaliseActivityPopUp(
            initialTitle = selectedActivity!!.name,
            initialNotes = "",
            onDismiss = {title, notes, uris -> addActivity(title, notes, uris)})
    }

    Scaffold (containerColor = MaterialTheme.colorScheme.secondary) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                with(viewState) {
                    when (this) {
                        ViewState.Loading, ViewState.RevokedPermissions -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(360.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is ViewState.Success -> {
                            LaunchedEffect(Unit) {
                                timerPaused = false
                            }

                            val currentLocation = this.location
                            val cameraPositionState = rememberCameraPositionState()
                            LaunchedEffect(currentLocation) {
                                if (currentLocation != null) {
                                    if (!timerPaused)
                                        locations.add(currentLocation)

                                    cameraPositionState.centerOnLocation(currentLocation)
                                }
                            }
                            Map(cameraPositionState, mapMaximised = mapMaximised) {
                                mapMaximised = !mapMaximised
                            }
                        }
                    }
                }
                if (!mapMaximised) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = rectangleContainerModifier) {
                        Text(
                            timeString, color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.align(Alignment.Center), fontSize = 64.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (selectedCard == 0) {
                        StatsCard(
                            value = distanceString, unit = distanceUnit.toString(), index = 0,
                            totalCards = totalCards, incrementSelectedCard = incrementSelectedCard
                        )
                    } else if (selectedCard == 1) {
                        StatsCard(
                            value = "15:03", unit = "/$distanceUnitCorrected", index = 1,
                            totalCards = totalCards, incrementSelectedCard = incrementSelectedCard
                        )
                    } else if (caloriesEnabled == true && selectedCard == 2) {
                        StatsCard(
                            value = calorieString, unit = calorieUnit.toString(), index = 2,
                            totalCards = totalCards, incrementSelectedCard = incrementSelectedCard
                        )
                    }
                }
            }

            // pause and stop controls, align to bottom of box
            Box (modifier = Modifier
                .width((screenWidth / 3) * 2)
                .padding(bottom = 10.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(55.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    onClick = {
                        timerPaused = true
                        showFinalisationPopUp = true
                    }
                ) {
                    Icon(
                        Icons.Filled.Stop, "Finish Activity",
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(50.dp)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(85.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    onClick = {
                        with (viewState) {
                            timerPaused = when (this) {
                                is ViewState.Success -> { !timerPaused }
                                else -> { true }
                            }
                        }
                    }
                ) {
                    if (timerPaused) {
                        Icon(
                            Icons.Filled.PlayArrow, "Resume Activity",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Pause, "Pause Activity",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(value: String, unit: String, index: Int, totalCards: Int, incrementSelectedCard: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(116.dp)
        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
        .clickable { incrementSelectedCard() }
    ) {
        Text(
            value, color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.Center), fontSize = 64.sp
        )
        Text(
            unit, color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(10.dp)
        )
        Steppers(totalCards, index,
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp))
    }
}

@Composable
private fun Map(cameraPositionState: CameraPositionState, mapMaximised: Boolean, toggleMaximisation: () -> Unit) {
    // nasty way to check, but fetching from settings is too slow
    val darkModeEnabled = MaterialTheme.colorScheme.secondary == DarkBlue

    Box {
        GoogleMap (
            modifier = Modifier
                .fillMaxWidth()
                .height(if (mapMaximised) 612.dp else 360.dp)
                .clip(RoundedCornerShape(20.dp)),
            cameraPositionState = cameraPositionState,
            properties = if (darkModeEnabled) {
                MapProperties(isMyLocationEnabled = true)
            } else {
                MapProperties(isMyLocationEnabled = true, mapType = MapType.NORMAL)
            },
            googleMapOptionsFactory = { if (darkModeEnabled) GoogleMapOptions().mapId("80a2ba4bcd1dc68f") else GoogleMapOptions() }
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(Color.White, CircleShape),
            onClick = { toggleMaximisation() }
        ) {
            if (mapMaximised) {
                Icon(Icons.Outlined.CloseFullscreen, contentDescription = "Minimise Map", tint = Color.Black)
            } else {
                Icon(Icons.Outlined.OpenInFull, contentDescription = "Maximise Map", tint = Color.Black)
            }
        }
    }
}