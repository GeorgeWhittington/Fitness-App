package com.foxden.fitnessapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

@Composable
fun HomeScreen(navigation: NavController) {
    val image = painterResource(R.drawable.fox)
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
    }
}