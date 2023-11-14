package com.foxden.fitnessapp.ui

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.foxden.fitnessapp.data.Activity
import com.foxden.fitnessapp.data.FitnessAppDatabase

fun loadActivities(application: Application) : List<Activity> {
    return FitnessAppDatabase.getDatabase(application).activityDao().getAll();
}
@Composable
fun ActivityString(activity: Activity, modifier: Modifier = Modifier) {
    Text(text = "Activity ${activity.id} ", modifier)
}

@Composable
fun ActivityList(activityList: List<Activity>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(activityList) {
                a -> ActivityString(activity = a, modifier)
        }
    }
}

@Composable
fun HomeScreen(navigation: NavController, application: Application) {
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
        var tmp = 0;

        // Get activities
        var activityList = loadActivities(application)
        ActivityList(activityList = activityList)

        Button(onClick = {
            val db = FitnessAppDatabase.getDatabase(application)
            val dao = db.activityDao()
            dao.insertAll(Activity(0, "Test", 0, 0, 1.0f, 69.0f))
            tmp++
            activityList = loadActivities(application)
        }
            ) {
            Text(text = "Add")
        }

        Button(onClick = { navigation.navigate(Routes.LOGIN_SCREEN) }) {
            Text(text = "Login")
        }
    }
}