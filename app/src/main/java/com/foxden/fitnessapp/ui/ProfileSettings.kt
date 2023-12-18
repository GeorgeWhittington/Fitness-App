package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Height
import androidx.compose.ui.graphics.Color
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.theme.MainColourScheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(navigation: NavController) {
    val image = painterResource(R.drawable.fox)
    var isModified by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {BackIcon{navigation.navigate(Routes.SETTINGS_SCREEN)}},
                actions = {
                    
                    SaveOption(isModified = isModified) {
                    // Perform save action here

                    isModified = false
                }},
                backgroundColor = Color.White,
                modifier = Modifier.height(56.dp)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Profile", color = Color.Black, fontSize = 20.sp)
            }

        },
        bottomBar = { NavBar(navigation = navigation)}
    ) { innerPadding->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ){


            Image(
                painter = image,
                contentDescription = stringResource(id = R.string.fox_alt_text),
                modifier = Modifier.size(width = 100.dp, height = 100.dp) // Adjust width and height as needed
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Personal" ,fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            NameInput { isModified = true }
            Spacer(modifier = Modifier.height(20.dp))
            WeightInput()
            Spacer(modifier = Modifier.height(20.dp))
            HeightInput()
            


        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(onChange: () -> Unit) {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    val maxInputLength = 20
    Row(
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            Icons.Outlined.Person,
            contentDescription = "person icon",
            modifier = Modifier.padding(end = 10.dp)
        )


        TextField(
            modifier = Modifier
                .weight(1f) // Take up remaining space in the row
                //.widthIn(max = 200.dp)
                .padding(end = 16.dp),
            value = textState,
            onValueChange = {
                if (it.text.length <= maxInputLength) {
                    textState = it
                    onChange() // Notify parent about modification
                }
            },
            placeholder = { Text("Name") },
            singleLine = true,
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightInput() {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    Row(
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            Icons.Outlined.MonitorWeight,
            contentDescription = "weight icon",
            modifier = Modifier.padding(end = 10.dp)
            )


        TextField(

            value = textState,
            onValueChange = { textState = it },
            placeholder = { Text("Current weight") }
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightInput() {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    Row(
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            Icons.Outlined.Height,
            contentDescription = "height icon",
            modifier = Modifier.padding(end = 10.dp)
            )

        TextField(

            value = textState,
            onValueChange = { textState = it },
            placeholder = { Text("Height") }
        )

    }

}


