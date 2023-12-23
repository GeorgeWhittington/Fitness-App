package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.Routes
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.NavBar
import kotlinx.coroutines.flow.first

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigation: NavController, application: Application) {

    //link to datastore
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)

    //used to check which character
    var character by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var image = if(character == "Fox"){
        R.drawable.fox
    } else if(character == "Racoon"){
        R.drawable.racoon
    } else{ R.drawable.cat
    }

    LaunchedEffect(Unit) {
        GetHomeData(dataStoreManager,

            onCharacterLoaded = { loadedCharacter ->
                character = loadedCharacter
                isLoading = false

            }

        )

    }

    Scaffold (
        bottomBar = { NavBar(navigation = navigation) }
    ) {

        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoading) {
                Image(
                    painter = painterResource(image),
                    contentDescription = stringResource(id = R.string.cat_alt_text),
                    modifier = Modifier.size(width = 100.dp, height = 100.dp)
                )
            } else {
                Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.padding(end = 10.dp))
            }
            Text(text = "activities: 0 | distance: 0km | time: 0h 0m")
            Text(text = "[Recent Activity/Recent Activity with personal best]", Modifier.padding(10.dp))
            Text(text = "[card with statistics]", Modifier.padding(10.dp))

            Button(onClick = { navigation.navigate(Routes.DBTEST_SCREEN) }) {
                Text(text = "DBTest")
            }
        }
    }


}

suspend fun GetHomeData (
    dataStoreManager: SettingsDataStoreManager,

    onCharacterLoaded: (String) -> Unit
){


    val character = dataStoreManager.getStringSetting("CharacterKey", "Fox").first()
    onCharacterLoaded(character)
}