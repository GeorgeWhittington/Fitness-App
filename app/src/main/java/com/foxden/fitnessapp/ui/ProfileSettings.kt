package com.foxden.fitnessapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.R
import com.foxden.fitnessapp.data.SettingsDataStoreManager
import com.foxden.fitnessapp.ui.components.NavBar
import kotlinx.coroutines.flow.first

/*
ProfileSettings()

allows the user to enter details (name,weight,height)

these details are then saved to datastore preferences
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileSettings(navigation: NavController) {
    //Saving and retrieving data:
    //used for save option
    var isModified by remember { mutableStateOf(false) }

    //link to datastore
    val context = LocalContext.current
    val dataStoreManager = SettingsDataStoreManager(context)
    //used for getting and saving the data to datastore
    val triggerSave = remember { mutableStateOf(false) }
    var currentName by rememberSaveable { mutableStateOf("") }
    var currentWeight by rememberSaveable { mutableFloatStateOf(0f) }
    var currentHeight by rememberSaveable { mutableFloatStateOf(0f) }
    var weightUnit by rememberSaveable { mutableStateOf("") }
    var heightUnit by rememberSaveable { mutableStateOf("") }


    //used to get character option from datastore and load
    var character by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val image = when (character) {
        "Fox" -> { R.drawable.fox_happy }
        "Racoon" -> { R.drawable.racoon }
        "Cat" -> { R.drawable.hendrix_window }
        else -> { R.drawable.fox_happy }
    }

    // update variables once data has been collected from datastore
    LaunchedEffect(Unit) {
        GetProfileData(dataStoreManager,
            onWeightUnitLoaded = { loadedWeightUnit ->
                weightUnit = loadedWeightUnit
            },
            onHeightUnitLoaded = { loadedHeightUnit ->
                heightUnit = loadedHeightUnit
            },
            onNameLoaded = { loadedName ->
                currentName = loadedName},
            onWeightLoaded = { loadedWeight ->
                currentWeight = loadedWeight},
            onHeightLoaded = { loadedHeight ->
                currentHeight = loadedHeight},
            onCharacterLoaded = { loadedCharacter ->
                character = loadedCharacter
                isLoading = false
            }
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {BackIcon{navigation.popBackStack()}},

                //the save option appears in the top bar once 'isModified' value is set to true by an action
                actions = {
                    SaveOption(isModified = isModified) {
                        triggerSave.value = true
                        isModified = false
                    }
                    //once the save option is clicked -> causes data to be saved
                    LaunchedEffect(triggerSave.value) {
                        if (triggerSave.value) {
                            val weightToSave = if (weightUnit == "lbs") convertLbsToKg(currentWeight) else currentWeight
                            val heightToSave = if (heightUnit == "Cm") convertCmToFeet(currentHeight) else currentHeight

                            dataStoreManager.saveStringSetting("UserNameKey", currentName)
                            dataStoreManager.saveFloatSetting("UserWeightKey", weightToSave)
                            dataStoreManager.saveFloatSetting("UserHeightKey", heightToSave)

                            triggerSave.value = false
                        }
                }},
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.height(56.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Profile", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 20.sp)
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

            // if the character image is waiting to be loaded, display a placeholder image
            if (!isLoading) {
                Image(
                    painter = painterResource(image),
                    contentDescription = stringResource(id = R.string.animal_alt_text),
                    modifier = Modifier.size(width = 200.dp, height = 200.dp)
                )
            } else {
                Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.padding(end = 10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Personal", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Used for user to input a name as a string
            ProfileInputField(
                icon = Icons.Outlined.Person,
                placeholder = "Name",
                textValue = currentName,
                onTextChange = { currentName = it },
                onChange = { isModified = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

            //used for user to input weight
            FloatInputField(
                icon = Icons.Outlined.MonitorWeight,
                placeholder = "Weight",
                value = currentWeight,
                onValueChange = { newValue -> currentWeight = newValue },
                unit = weightUnit,
                min = if (weightUnit == "Kg") 30f else 66f,
                max = if (weightUnit == "Kg") 300f else 500f,
                onChange = { isModified = true },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(20.dp))

            //used for user to input height
            FloatInputField(
                icon = Icons.Outlined.Height,
                placeholder = "Height",
                value = currentHeight,
                onValueChange = { newValue -> currentHeight = newValue },
                unit = heightUnit,
                min = if (heightUnit == "Cm") 100f else 3f,
                max = if (heightUnit == "Cm") 250f else 8f,
                maxDigits = if (heightUnit == "Ft") 3 else 6,
                onChange = { isModified = true },
                keyboardType = KeyboardType.Number
            )
        }
    }
}

/*
GetProfileData()

retrieves the needed data from datastore
 */
suspend fun GetProfileData (
    dataStoreManager: SettingsDataStoreManager,
    onNameLoaded: (String) -> Unit,
    onWeightLoaded: (Float) -> Unit,
    onHeightLoaded: (Float) -> Unit,
    onWeightUnitLoaded: (String) -> Unit,
    onHeightUnitLoaded: (String) -> Unit,
    onCharacterLoaded: (String) -> Unit
){

    val name = dataStoreManager.getStringSetting("UserNameKey", "Name").first()
    onNameLoaded(name)
    val currentWeight = dataStoreManager.getFloatSetting("UserWeightKey", 0f).first()
    onWeightLoaded(currentWeight)
    val currentHeight = dataStoreManager.getFloatSetting("UserHeightKey", 0f).first()
    onHeightLoaded(currentHeight)
    val weightUnit = dataStoreManager.getStringSetting("WeightUnitKey", "Kg").first()
    onWeightUnitLoaded(weightUnit)
    val heightUnit = dataStoreManager.getStringSetting("HeightUnitKey", "Ft").first()
    onHeightUnitLoaded(heightUnit)
    val character = dataStoreManager.getStringSetting("CharacterKey", "Fox").first()
    onCharacterLoaded(character)
}
fun convertLbsToKg(lbs: Float): Float = lbs / 2.20462f
fun convertCmToFeet(cm: Float): Float = cm /30.48f
fun convertKgToLbs(kg: Float): Float = kg * 2.20462f
fun convertFeetToCm(feet: Float): Float = feet * 30.48f

/*
ProfileInputField()

allows the user to input a name : checks name is within acceptable range
 */
@Composable
fun ProfileInputField(
    icon: ImageVector,
    placeholder: String,
    textValue: String,
    onTextChange: (String) -> Unit,
    onChange: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var textState by remember { mutableStateOf(TextFieldValue(textValue)) }
    val maxInputLength = 20

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, contentDescription = null, modifier = Modifier.padding(end = 10.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            value = textState,
            onValueChange = {
                if (it.text.length <= maxInputLength) {
                    textState = it
                    onTextChange(it.text)
                    onChange()
                }
            },
            placeholder = { Text(textValue) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

/*
FloatInputField()

allows the user to input a float value : checks it is within acceptable range

converts the data from stored value to prefered unit

Example:

Height is stored in database as feet, if a user has height unit set to Cm, the page converts data to Cm
 */
@Composable
fun FloatInputField(
    icon: ImageVector,
    placeholder: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    unit: String,
    min: Float,
    max: Float,
    maxDigits: Int = 6,
    onChange: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    var textState by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        val convertedValue = if (unit == "lbs") {
            convertKgToLbs(value)
        } else if (unit == "Cm") {
            convertFeetToCm(value)
        } else {
            value
        }

        Icon(
            icon, contentDescription = null, modifier = Modifier.padding(end = 10.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            value = textState,
            onValueChange = { inputText ->
                if (inputText.length <= maxDigits) {
                    textState = inputText
                    val number = inputText.toFloatOrNull()
                    isError = number == null || number < min || number > max
                    if (!isError && number != null) {
                        onValueChange(number)
                        onChange()
                    }
                }
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder = { Text("%.2f".format(convertedValue)) },
            singleLine = true,
            trailingIcon = { Text(unit) }
        )
    }

    if (isError) {
        Text("Enter a valid $placeholder", color = MaterialTheme.colorScheme.error)
    }
}


