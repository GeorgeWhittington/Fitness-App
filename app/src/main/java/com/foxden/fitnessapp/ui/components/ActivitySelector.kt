package com.foxden.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foxden.fitnessapp.ui.ActivityType
import com.foxden.fitnessapp.ui.activities
import com.foxden.fitnessapp.ui.theme.LightBlue
import com.foxden.fitnessapp.utils.DrawScrollableView

@OptIn(ExperimentalComposeUiApi::class)
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
                        activities.filter { it.name.startsWith(searchQuery) }.forEach {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp, vertical = 20.dp)
                                    .clickable (
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
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
                                // TODO: If an activity was added by the user, add a trash icon to allow them to delete it?
                                // or maybe an edit icon if there's time?
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
                            .clickable (
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { setSelectedActivity(it) },
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