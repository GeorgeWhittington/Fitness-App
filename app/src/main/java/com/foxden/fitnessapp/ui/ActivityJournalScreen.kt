package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.ui.components.ActivityWidget
import com.foxden.fitnessapp.ui.components.NavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityJournalScreen(navigation: NavController) {
    Scaffold (
        containerColor = Color(134, 187, 216),
        bottomBar = { NavBar(navigation = navigation) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(25.dp)) {
            Row (
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Activity Journal", fontSize = 20.sp,
                    color = Color(11, 45, 61)
                )
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.offset(x = 9.dp, y = (-9).dp)) {
                    Icon(
                        Icons.Outlined.Tune, contentDescription = "Sort and Filter",
                        tint = Color(11, 45, 61), modifier = Modifier.size(30.dp)
                    )
                }
            }

            // TODO: Make this a LazyColumn when data is read in from db!
            Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()
                Spacer(modifier = Modifier.size(10.dp))
                ActivityWidget()

                // Important! brings the scrollable window above the 50dp high navbar
                Spacer(modifier = Modifier.size(50.dp))
            }
        }
    }

}

@Preview
@Composable
fun PreviewActivityJournal() {
    val navController = rememberNavController()
    ActivityJournalScreen(navigation = navController)
}