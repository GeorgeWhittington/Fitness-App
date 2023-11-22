package com.foxden.fitnessapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.DBHelper

fun loadData(activityList: MutableList<ActivityLog>, dbHelper: DBHelper) {
    activityList.clear()
    for (a in ActivityLogDAO.fetchAll(dbHelper.readableDatabase)) {
        activityList.add(a)
    }
}
@Composable
fun DBTestScreen(navigation: NavController, dbHelper: DBHelper) {

    var activityList = remember { mutableStateListOf<ActivityLog>() }
    loadData(activityList, dbHelper)

    Column {
        Button(onClick = {
            val a = ActivityLog()
            a.name = "Test Name"
            ActivityLogDAO.insert(dbHelper.writableDatabase, a)
            loadData(activityList, dbHelper)
        }) {
            Text(text = "Insert Data")
        }

        /*
        Button(onClick =  {loadData(activityList, dbHelper) }) {
            Text(text = "Load Data")
        }
        */

        Column {
            for (a in activityList) {
                Text("Activity - id: ${a.id} title:${a.name}")
            }
        }

    }

}