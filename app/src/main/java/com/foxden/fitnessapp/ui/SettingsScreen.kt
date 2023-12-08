package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.Routes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigation: NavController) {
    Scaffold(
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
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
            PushNotifOption()
            GPSOption()
            Spacer(modifier = Modifier.height(50.dp))
            DeleteButton()
        }
    }
}

@Composable
fun PageName(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        //Spacer(modifier = Modifier.weight(1f))
        Text(
            text = text, color = Color.Black, fontSize = 20.sp,
            //modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
        )
        //Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun DeleteButton(){
    Button( onClick = { /*do*/ }) {

        Text(text = "Delete all data")
        Icon(
            Icons.Outlined.Delete,
            contentDescription = "bin icon",
            tint = Color.White
        )
    }

}

@Composable
fun RowDivider(
    height: Int = 1,
    color: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    Divider(modifier = modifier.fillMaxWidth(), color = color)
}



@Composable
fun PushNotifOption() {
    var checkedState by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Push notifications", color = Color.Black,
            fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(checked = checkedState, onCheckedChange = { checkedState = it })
    }
}

@Composable
fun GPSOption() {
    var checkedState by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = "GPS tracking", color = Color.Black,
            fontSize = 16.sp, modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checkedState,
            onCheckedChange = { checkedState = it }
        )
    }
}

@Composable
fun RowOption(text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }.padding(vertical = 16.dp)
    ) {
        Text(
            text = text, color = Color.Black,
            fontSize = 16.sp, modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Outlined.ArrowForwardIos,
            contentDescription = "next arrow"
        )
    }
}