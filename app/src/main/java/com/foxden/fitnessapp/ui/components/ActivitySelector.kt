package com.foxden.fitnessapp.ui.components

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.foxden.fitnessapp.ui.theme.LightBlue

// Temporary class for dummy data
class ActivityType(
    val name: String,
    val icon: ImageVector,
    val gps_tracked: Boolean
)

val activities = arrayOf(
    ActivityType("Jogging", Icons.Outlined.DirectionsRun, true),
    ActivityType("Hiking", Icons.Outlined.Hiking, true),
    ActivityType("Cycling", Icons.Outlined.DirectionsBike, true),
    ActivityType("Yoga", Icons.Outlined.SelfImprovement, false),
    ActivityType("Weightlifting", Icons.Outlined.FitnessCenter, false),
    ActivityType("Swimming", Icons.Outlined.Pool, true)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ActivitySelector(selectedActivity: ActivityType?, setSelectedActivity: (ActivityType?) -> Unit) {
    var searching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    var containerModifier = Modifier
        .fillMaxWidth()
        .height(279.dp)
        .focusable()

    containerModifier = if (searching) {
        containerModifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    } else {
        containerModifier.clip(RoundedCornerShape(20.dp))
    }

    Column (
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // search text field
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { searching = it.isFocused },
            value = searchQuery, onValueChange = { searchQuery = it },
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            placeholder = { Text("Select Activity") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0, 0, 0,0),
                focusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                setSelectedActivity(activities.firstOrNull { it.name == searchQuery } ?: selectedActivity)
                searchQuery = ""
                focusManager.moveFocus(FocusDirection.Exit)
            })
        )

        if (searching) {
            // TODO: Make this a LazyColumn when actually getting the data from db
            DrawScrollableView(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Column {
                        // TODO: change results based on query
                        activities.forEach {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp, vertical = 20.dp)
                                    .clickable {
                                        searchQuery = ""
                                        searching = false
                                        setSelectedActivity(it)
                                        focusManager.moveFocus(FocusDirection.Exit)
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Icon(it.icon, null)
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Text(it.name)
                                }
                                if (it == selectedActivity)
                                    Icon(Icons.Outlined.Check, null)
                            }
                        }
                    }
                }
            )
        }

        if (!searching) {
            val activitiesSelectedFirst = remember {
                mutableStateListOf<ActivityType>(activities[0], activities[1], activities[2])
            }

            LaunchedEffect(selectedActivity) {
                if (activitiesSelectedFirst.contains(selectedActivity))
                    return@LaunchedEffect

                activitiesSelectedFirst.clear()
                activitiesSelectedFirst.add(selectedActivity ?: activities.first())
                for (activity in activities) {
                    if (activitiesSelectedFirst.contains(activity))
                        continue

                    activitiesSelectedFirst.add(activity)

                    if (activitiesSelectedFirst.size == 3)
                        break
                }
            }

            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                activitiesSelectedFirst.forEach {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .clickable { setSelectedActivity(it) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(it.icon, null)
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(it.name)
                        }
                        if (it == selectedActivity)
                            Icon(Icons.Outlined.Check, null)
                    }
                }
            }
        }
    }
}

// TODO: Possibly move this elsewhere. utils?
// code from: https://medium.com/@ajitesh.gupta56/scroll-bars-in-jetpack-compose-android-ff787f9b522
@Composable
fun DrawScrollableView(content: @Composable () -> Unit, modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = {
            val scrollView = ScrollView(it)
            val layout = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scrollView.layoutParams = layout
            scrollView.isVerticalFadingEdgeEnabled = true
            scrollView.isScrollbarFadingEnabled = false
            scrollView.addView(ComposeView(it).apply {
                setContent {
                    content()
                }
            })
            val linearLayout = LinearLayout(it)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            linearLayout.addView(scrollView)
            linearLayout
        }
    )
}

@Preview
@Composable
fun PreviewActivitySelector() {
    var selectedActivity: ActivityType? by rememberSaveable { mutableStateOf(null) }

    Box (modifier = Modifier
        .height(400.dp)
        .background(LightBlue)
    ) {
        ActivitySelector(selectedActivity, setSelectedActivity = { selectedActivity = it })
    }
}