package com.foxden.fitnessapp.ui

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigation: NavController, application: Application) {
    val image = painterResource(R.drawable.fox)

    Scaffold (
        bottomBar = { NavBar(navigation = navigation) }
    ) {innerPadding ->
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = image,
                contentDescription = stringResource(id = R.string.fox_alt_text)
            )
            Text(text = "activities: 0 | distance: 0km | time: 0h 0m")
            Text(text = "[Recent Activity/Recent Activity with personal best]", Modifier.padding(10.dp))
            Text(text = "[card with statistics]", Modifier.padding(10.dp))

            Button(onClick = { navigation.navigate(Routes.LOGIN_SCREEN) }) {
                Text(text = "Login")
            }

            Button(onClick = { navigation.navigate(Routes.DBTEST_SCREEN) }) {
                Text(text = "DBTest")
            }

            Button(onClick = { navigation.navigate(Routes.MAIN_SETTINGS_SCREEN) }) {
                Text(text = "Settings")
            }
        }
    }


}