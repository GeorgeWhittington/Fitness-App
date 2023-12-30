package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.ActivityWidget
import com.foxden.fitnessapp.ui.components.NavBar
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigation: NavController, application: Application, dbHelper: DBHelper) {

    //link to datastore
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)

    //distance unit
    var distanceUnit by rememberSaveable { mutableStateOf("") }

    //used to load chosen character
    var character by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var image = if(character == "Fox"){
        R.drawable.fox
    } else if(character == "Racoon"){
        R.drawable.racoon
    } else{ R.drawable.cat
    }

    //activity logs
    var ActivityLogList = remember {
        ActivityLogDAO.fetchAll(dbHelper.writableDatabase).sortedBy { it.startTime }.toMutableStateList()
    }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")


    //activity type
    var activityTypeList = remember {
        ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList()
    }

    LaunchedEffect(Unit) {
        GetHomeData(dataStoreManager,

            onCharacterLoaded = { loadedCharacter ->
                character = loadedCharacter
                isLoading = false

            }
            ,
            onDistanceUnitLoaded = { loadedDistanceUnit ->
                distanceUnit = loadedDistanceUnit
            },

        )

    }

    Scaffold (
        bottomBar = { NavBar(navigation = navigation) }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (modifier = Modifier.height(350.dp)){
                if (!isLoading) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = stringResource(id = R.string.cat_alt_text),
                        //modifier = Modifier.size(width = 150.dp, height = 150.dp)
                    )
                } else {
                    Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.padding(end = 10.dp))
                }
            }
            Text(text = "activities: 0 | distance: 0km | time: 0h 0m")

            /*
            Button(onClick = { navigation.navigate(Routes.DBTEST_SCREEN) }) {
                Text(text = "DBTest")
            }
             */

            if (ActivityLogList.isNotEmpty()) {
                val lastActivity = ActivityLogList.last()
                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Recent activity", fontSize = 20.sp)
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))

                ActivityWidget(lastActivity, activityTypeList.filter{ it.id ==  lastActivity.activityTypeId}.first(),distanceUnit=distanceUnit)
            }




        }
    }


}

suspend fun GetHomeData (
    dataStoreManager: SettingsDataStoreManager,

    onCharacterLoaded: (String) -> Unit,
    onDistanceUnitLoaded: (String) -> Unit,
){


    val character = dataStoreManager.getStringSetting("CharacterKey", "Fox").first()
    onCharacterLoaded(character)
    val distanceUnit = dataStoreManager.getStringSetting("DistanceUnitKey", "Miles").first()
    onDistanceUnitLoaded(distanceUnit)
}