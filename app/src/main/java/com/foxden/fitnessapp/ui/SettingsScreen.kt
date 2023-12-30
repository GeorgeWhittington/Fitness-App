package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.theme.DarkBlue


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigation: NavController, dbHelper: DBHelper) {
    Scaffold(
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RowOption("Profile") { navigation.navigate(Routes.PROFILE_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))
            RowOption("Goals") { navigation.navigate(Routes.GOALS_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))
            RowOption("Display") { navigation.navigate(Routes.DISPLAY_SETTINGS_SCREEN) }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))

            GPSOption()
            Spacer(modifier = Modifier.height(50.dp))
            DeleteButton(dbHelper)


        }
    }
}

@Composable
fun PageName(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SaveOption(isModified: Boolean, onClick: () -> Unit) {

    if (isModified) {
        //Spacer(modifier = androidx.compose.ui.Modifier.weight(1f))
        TextButton(
            onClick = { onClick() }
        ) {
            Text(text = "Save",
                color = DarkBlue)
        }
    }

}

@Composable
fun BackIcon(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() }, // Replace "home" with your destination route
        modifier = Modifier
        //.padding(start = 16.dp)
        //.clickable { navController.navigate("home") } // Clickable modifier for the IconButton
    ) {
        Icon(
            Icons.Outlined.ChevronLeft, contentDescription = "back arrow",
            //modifier = Modifier.padding(start = 16.dp),
            tint = DarkBlue
        )
    }
}


@Composable
fun DeleteButton(dbHelper: DBHelper) {
    var isClicked by remember { mutableStateOf(false) }

    Button(
        onClick = {
            dbHelper.deleteAndReset()
            isClicked = true
        }
    ) {

         Text(text = "Delete all data")


        Icon(
            Icons.Outlined.Delete,
            contentDescription = "bin icon",
            tint = Color.White
        )
    }
    if (isClicked) {
        Text(text = "Data deleted", color = Color )
    }
}

@Composable
fun RowDivider(
    height: Int = 1,

    modifier: Modifier = Modifier
) {
    Divider(modifier = modifier.fillMaxWidth())
}



@Composable
fun PushNotifOption() {
    var checkedState by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Push notifications",
            fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(checked = checkedState, onCheckedChange = { checkedState = it })
    }
}

@Composable
fun GPSOption() {
    var checkedState by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = "GPS tracking",
            fontSize = 16.sp, modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checkedState,
            onCheckedChange = { checkedState = it }
        )
    }
}

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


        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 10.dp))
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
        Text("Enter a valid $placeholder", color = MaterialTheme.colors.error)
    }
}
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
            Icons.Outlined.ArrowForwardIos,
            contentDescription = "next arrow"
        )
    }
}

