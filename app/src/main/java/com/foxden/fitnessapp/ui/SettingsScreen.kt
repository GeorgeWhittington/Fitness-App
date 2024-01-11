package com.foxden.fitnessapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.GoalDAO
import com.foxden.fitnessapp.ui.components.NavBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.foxden.fitnessapp.utils.LocationViewModel
import com.foxden.fitnessapp.utils.PermissionEvent
import com.foxden.fitnessapp.utils.ViewState

/*
SettingsScreen()

The main page for settings.
Offers the user to navigate to other settings pages/ change GPS option
 */
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navigation: NavController, locationViewModel: LocationViewModel, dbHelper: DBHelper) {

    //GPS Option: allows the user to turn GPS off/on
    var alreadyAskedPermission by rememberSaveable { mutableStateOf(false) }
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)) { alreadyAskedPermission = true }
    val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Granted) } }
        !permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) { locationViewModel.handle(PermissionEvent.Revoked) } }
    }

    var gpsCheckedState by rememberSaveable { mutableStateOf(false) }

    with(viewState) {
        gpsCheckedState = when (this) {
            ViewState.Loading, is ViewState.Success -> { true }
            ViewState.RevokedPermissions -> { false }
        }
    }

    Scaffold(
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Pages: uses a composable to create a clickable row to navigate to other pages
            RowOption("Profile") { navigation.navigate(Routes.PROFILE_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))
            RowOption("Goals") { navigation.navigate(Routes.GOALS_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))
            RowOption("Display") { navigation.navigate(Routes.DISPLAY_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))

            //GPS switch
            GPSOption(gpsCheckedState) {
                if (it && !alreadyAskedPermission) {
                    permissionState.launchMultiplePermissionRequest()
                } else if (it && alreadyAskedPermission) {
                    ContextCompat.startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
                } else {

                    ContextCompat.startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            DeleteButton(dbHelper)
        }
    }
}

/*
RowOption()

Used for a clickable row to navigate to different pages
 */
@Composable
fun RowOption(text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp, modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Outlined.ArrowForwardIos, contentDescription = "next arrow",
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}


/*
DeleteButton()

Used to be able to delete the user's data stored within the database
 */
@Composable
fun DeleteButton(dbHelper: DBHelper) {
    var isClicked by remember { mutableStateOf(false) }

    if (isClicked) {
        delDataPopup(
            onConfirm = {
                isClicked = false
                dbHelper.deleteAndReset()
            },
            onCancel = { isClicked = false }
        )
    }

    Button(onClick = { isClicked = true }) {
        Text(text = "Delete all data")
        Icon(
            Icons.Outlined.Delete,
            contentDescription = "bin icon",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun delDataPopup(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { androidx.compose.material.Text("Delete Data") },
        text = { androidx.compose.material.Text("Are you sure you want to delete data?") },
        confirmButton = {
            androidx.compose.material.Button(onClick = onConfirm) {
                androidx.compose.material.Text("Confirm")
            }
        },
        dismissButton = {
            androidx.compose.material.Button(onClick = onCancel) {
                androidx.compose.material.Text("Cancel")
            }
        }
    )
}


/*
GPSOption()

GPS switch: used for turning GPS off and on
 */
@Composable
fun GPSOption(checkedState: Boolean, setCheckedState: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "GPS tracking",
            fontSize = 16.sp, modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checkedState,
            onCheckedChange = { setCheckedState(it) }
        )
    }
}


//Shared composables - used by other setting pages

/*
RowDivider()

Used for a divider which has a specific modifier
 */
@Composable
fun RowDivider(modifier: Modifier = Modifier) {
    Divider(modifier = modifier.fillMaxWidth())
}



/*
SaveOption()

Composable used on other setting's pages.

Used for a clickable save text which will save preferences based on certain input text/dropdowns changing
 */
@Composable
fun SaveOption(isModified: Boolean, onClick: () -> Unit) {
    if (isModified) {
        TextButton(onClick = { onClick() }) {
            Text(text = "Save", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

/*
BackIcon()

used in other pages to go back to the mains settings page

creates a clickable icon which performs an action once clicked.
 */
@Composable
fun BackIcon(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }, // Replace "home" with your destination route
        modifier = Modifier
    ) {
        Icon(
            Icons.Outlined.ChevronLeft, contentDescription = "back arrow",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

/*
IntInputField()

Used for a text input of type Int (used for calorie goal)

allows an input within a certain range, uses placeholders
 */
@Composable
fun IntInputField(
    icon: ImageVector,
    placeholder: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    unit: String,
    min: Int,
    max: Int,
    maxDigits: Int = 6,
    onChange: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    var textState by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, contentDescription = null, modifier = Modifier.padding(end = 10.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            value = textState,
            onValueChange = { inputText ->
                if (inputText.length <= maxDigits) {
                    textState = inputText
                    val number = inputText.toIntOrNull()
                    isError = number == null || number < min || number > max
                    if (!isError && number != null) {
                        onValueChange(number)
                        onChange()
                    }
                }
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder = { Text("$value") },
            singleLine = true,
            trailingIcon = { Text(unit) }
        )
    }

    if (isError) {
        Text("Enter a valid $placeholder", color = MaterialTheme.colorScheme.error)
    }
}


