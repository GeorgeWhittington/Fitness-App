package com.foxden.fitnessapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessibleForward
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.DownhillSkiing
import androidx.compose.material.icons.outlined.ElectricBike
import androidx.compose.material.icons.outlined.EmojiNature
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.GolfCourse
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.IceSkating
import androidx.compose.material.icons.outlined.Kayaking
import androidx.compose.material.icons.outlined.Kitesurfing
import androidx.compose.material.icons.outlined.NordicWalking
import androidx.compose.material.icons.outlined.Paragliding
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.RollerSkating
import androidx.compose.material.icons.outlined.Rowing
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.ScubaDiving
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Skateboarding
import androidx.compose.material.icons.outlined.Sledding
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material.icons.outlined.Snowshoeing
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Sports
import androidx.compose.material.icons.outlined.SportsBaseball
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material.icons.outlined.SportsFootball
import androidx.compose.material.icons.outlined.SportsGolf
import androidx.compose.material.icons.outlined.SportsGymnastics
import androidx.compose.material.icons.outlined.SportsHandball
import androidx.compose.material.icons.outlined.SportsHockey
import androidx.compose.material.icons.outlined.SportsKabaddi
import androidx.compose.material.icons.outlined.SportsMartialArts
import androidx.compose.material.icons.outlined.SportsMotorsports
import androidx.compose.material.icons.outlined.SportsRugby
import androidx.compose.material.icons.outlined.SportsScore
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material.icons.outlined.Surfing
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO: Should be in a constants file somewhere
val iconOptions: Map<String, ImageVector> = mapOf<String, ImageVector>(
    Pair("DirectionsBoat", Icons.Outlined.DirectionsBoat),
    Pair("FitnessCenter", Icons.Outlined.FitnessCenter),
    Pair("DirectionsRun", Icons.Outlined.DirectionsRun),
    Pair("DirectionsWalk", Icons.Outlined.DirectionsWalk),
    Pair("DirectionsBike", Icons.Outlined.DirectionsBike),
    Pair("Hiking", Icons.Outlined.Hiking),
    Pair("Pool", Icons.Outlined.Pool),
    Pair("Pets", Icons.Outlined.Pets),
    Pair("Flight", Icons.Outlined.Flight),
    Pair("AccessibleForward", Icons.Outlined.AccessibleForward),
    Pair("Anchor", Icons.Outlined.Anchor),
    Pair("Rowing", Icons.Outlined.Rowing),
    Pair("SelfImprovement", Icons.Outlined.SelfImprovement),
    Pair("SportsSoccer", Icons.Outlined.SportsSoccer),
    Pair("EmojiNature", Icons.Outlined.EmojiNature),
    Pair("SportsBasketball", Icons.Outlined.SportsBasketball),
    Pair("SportsKabaddi", Icons.Outlined.SportsKabaddi),
    Pair("Sports", Icons.Outlined.Sports),
    Pair("SportsTennis", Icons.Outlined.SportsTennis),
    Pair("Surfing", Icons.Outlined.Surfing),
    Pair("SportsMotorsports", Icons.Outlined.SportsMotorsports),
    Pair("SportsHandball", Icons.Outlined.SportsHandball),
    Pair("SportsBaseball", Icons.Outlined.SportsBaseball),
    Pair("SportsVolleyball", Icons.Outlined.SportsVolleyball),
    Pair("SportsFootball", Icons.Outlined.SportsFootball),
    Pair("DownhillSkiing", Icons.Outlined.DownhillSkiing),
    Pair("Kayaking", Icons.Outlined.Kayaking),
    Pair("Skateboarding", Icons.Outlined.Skateboarding),
    Pair("SportsCricket", Icons.Outlined.SportsCricket),
    Pair("SportsMartialArts", Icons.Outlined.SportsMartialArts),
    Pair("NordicWalking", Icons.Outlined.NordicWalking),
    Pair("SportsGolf", Icons.Outlined.SportsGolf),
    Pair("Paragliding", Icons.Outlined.Paragliding),
    Pair("Snowboarding", Icons.Outlined.Snowboarding),
    Pair("SportsGymnastics", Icons.Outlined.SportsGymnastics),
    Pair("Kitesurfing", Icons.Outlined.Kitesurfing),
    Pair("Snowshoeing", Icons.Outlined.Snowshoeing),
    Pair("SportsHockey", Icons.Outlined.SportsHockey),
    Pair("IceSkating", Icons.Outlined.IceSkating),
    Pair("SportsRugby", Icons.Outlined.SportsRugby),
    Pair("Sledding", Icons.Outlined.Sledding),
    Pair("ScubaDiving", Icons.Outlined.ScubaDiving),
    Pair("RollerSkating", Icons.Outlined.RollerSkating),
    Pair("Park", Icons.Outlined.Park),
    Pair("Sailing", Icons.Outlined.Sailing),
    Pair("ElectricBike", Icons.Outlined.ElectricBike),
    Pair("SportsScore", Icons.Outlined.SportsScore),
    Pair("Water", Icons.Outlined.Water),
    Pair("Spa", Icons.Outlined.Spa),
    Pair("BeachAccess", Icons.Outlined.BeachAccess),
    Pair("GolfCourse", Icons.Outlined.GolfCourse)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityTypeDialog(onDismiss: () -> Unit, onError: (String) -> Unit) {
    var activityName by remember { mutableStateOf("") }
    var selectedIcon: String? by remember { mutableStateOf(null) }
    var gpsTracking by remember { mutableStateOf(true) }
    var setTracking by remember { mutableStateOf(false) }

    var gpsTrackingSwitchEnabled by remember { mutableStateOf(true) }
    var setTrackingSwitchEnabled by remember { mutableStateOf(false) }
    val selectedIconVector = iconOptions[selectedIcon]
    var iconDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Add a new activity") },
        text = {
            Column {
                TextField(
                    value = activityName, onValueChange = {activityName = it},
                    placeholder = { Text("Activity Name") }
                )
                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = iconDropdownExpanded,
                    onExpandedChange = { iconDropdownExpanded = it }
                ) {
                    var leadingIcon: (@Composable () -> Unit)? = null
                    if (selectedIcon != null)
                        leadingIcon = @Composable { Icon(selectedIconVector!!, selectedIcon) }

                    var placeholder: (@Composable () -> Unit)? = @Composable { Text("Select an icon") }
                    if (selectedIcon != null)
                        placeholder = null

                    TextField(
                        modifier = Modifier.menuAnchor(), readOnly = true, value = "", onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconDropdownExpanded) },
                        leadingIcon = leadingIcon, placeholder = placeholder
                    )
                    ExposedDropdownMenu(
                        expanded = iconDropdownExpanded,
                        onDismissRequest = { iconDropdownExpanded = false }
                    ) {
                        iconOptions.forEach {iconOption ->
                            DropdownMenuItem(
                                text = {
                                    Box (
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(iconOption.value, iconOption.key)
                                    }
                               },
                                onClick = {
                                    selectedIcon = iconOption.key
                                    iconDropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GPS Tracking")
                    Switch(
                        checked = gpsTracking, enabled = gpsTrackingSwitchEnabled,
                        onCheckedChange = {checked ->
                            if (checked) {
                                gpsTracking = true
                                setTracking = false
                                setTrackingSwitchEnabled = false
                            } else {
                                gpsTracking = false
                                setTrackingSwitchEnabled = true
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Tracking")
                    Switch(
                        checked = setTracking, enabled = setTrackingSwitchEnabled,
                        onCheckedChange = {checked ->
                            if (checked) {
                                setTracking = true
                                gpsTracking = false
                                gpsTrackingSwitchEnabled = false
                            } else {
                                setTracking = false
                                gpsTrackingSwitchEnabled = true
                            }
                        }
                    )
                }
                // make smaller and in grey
                Text(text = "As in, a set of repetitions of an exercise",
                    fontSize = 12.sp, color = Color.Gray)
            }
        },
        onDismissRequest = { onDismiss() },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // verify input
                if (activityName.isEmpty()) {
                    onError("Your new activity must have a name")
                } else if (selectedIcon == null || !iconOptions.containsKey(selectedIcon)) {
                    onError("You must select an icon for your new activity")
                } else if (gpsTracking && setTracking) {
                    // This should never happen
                    Log.e("FIT", "GPS Tracking and Set tracking settings selected at the same time")
                    onError("ERROR: GPS tracking and set tracking are incompatible")
                } else {
                    addActivity(activityName, selectedIcon!!, gpsTracking, setTracking)
                    onDismiss()
                }
            }) {
                Text("Add")
            }
        }
    )
}

@Composable
fun AddActivityTypeErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Ok")
            }
        },
        title = { Text("Error") },
        text = { Text(errorMessage) }
    )
}

fun addActivity(
    name: String, icon: String,
    gpsTracking: Boolean, setTracking: Boolean
) {
    Log.d("FIT", "new activity type: name=${name}, icon=${icon}, gpsTracking=${gpsTracking}, setTracking=${setTracking}")
    // TODO: add to db
}

@Composable
@Preview
fun PreviewAddActivityTypePopup() {
    var showDialog by remember { mutableStateOf(true) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showDialog)
        AddActivityTypeDialog(
            onDismiss = {showDialog = false},
            onError = {showDialog = false; errorMessage = it; showErrorDialog = true})

    if (showErrorDialog)
        AddActivityTypeErrorDialog(errorMessage = errorMessage, onDismiss = { showErrorDialog = false })
}