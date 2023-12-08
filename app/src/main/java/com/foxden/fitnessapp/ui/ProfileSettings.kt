package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.R
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.MonitorWeight
import com.foxden.fitnessapp.ui.components.NavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(navigation: NavController) {
    val image = painterResource(R.drawable.fox)

    Scaffold (
        bottomBar = { NavBar(navigation = navigation)}
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(40.dp)

            ) {
                BackIcon{navigation.navigate(Routes.SETTINGS_SCREEN)}
                PageName(text= "Profile")
            }
            RowDivider()
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = image,
                contentDescription = stringResource(id = R.string.fox_alt_text),
                modifier = Modifier.size(width = 100.dp, height = 100.dp) // Adjust width and height as needed
            )
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Personal" ,fontSize = 20.sp,)
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                WeightIcon()
                NameInput()
            }




        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput() {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    Column {
        TextField(
            value = textState,
            onValueChange = { textState = it },
            placeholder = { Text("Name") }
        )
    }
}

@Composable
fun WeightIcon() {
    Icon(
        Icons.Outlined.MonitorWeight,
        contentDescription = "next arrow"
    )
}
