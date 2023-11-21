package com.foxden.fitnessapp.ui

import android.R
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.foxden.fitnessapp.database.Activity
import com.foxden.fitnessapp.database.DBHelper

fun loadData(activityList: MutableList<Activity>, dbHelper: DBHelper) {
    activityList.clear()
    for (a in Activity.getAll(dbHelper.readableDatabase)) {
        activityList.add(a)
    }
}
@Composable
fun DBTestScreen(navigation: NavController, dbHelper: DBHelper) {

    var activityList = remember { mutableStateListOf<Activity>() }
    loadData(activityList, dbHelper)

    Column {
        Button(onClick = {
            val a = Activity()
            a.title = "Test Title"
            Activity.insert(dbHelper.writableDatabase, a)
            loadData(activityList, dbHelper)
        }) {
            Text(text = "Insert Data")
        }

        /*
        Button(onClick =  {loadData(activityList, dbHelper) }) {
            Text(text = "Load Data")
        }
        */

        Button(onClick =  {
            Activity.deleteAll(dbHelper.writableDatabase)
            loadData(activityList, dbHelper) }) {
            Text(text = "Delete")
        }

        Column {
            for (a in activityList) {
                Text("Activity - id: ${a.id} title:${a.title}")
            }
        }

    }

}