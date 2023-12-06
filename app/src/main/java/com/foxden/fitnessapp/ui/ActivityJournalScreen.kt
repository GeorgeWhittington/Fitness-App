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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.foxden.fitnessapp.ui.components.ActivityWidget
import com.foxden.fitnessapp.ui.components.NavBar
import com.foxden.fitnessapp.ui.theme.DarkBlue
import com.foxden.fitnessapp.ui.theme.LightBlue

class DropdownOption(val text: String, val icon: ImageVector? = null)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActivityJournalScreen(navigation: NavController) {
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        BottomSheet() {
            showSheet = false
        }
    }

    Scaffold (
        containerColor = LightBlue,
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
                    color = DarkBlue
                )
                Row {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.offset(x = 9.dp, y = (-9).dp)
                    ) {
                        Icon(
                            Icons.Outlined.Add, contentDescription = "Add Activity Manually",
                            tint = DarkBlue, modifier = Modifier.size(30.dp))
                    }

                    IconButton(
                        onClick = { showSheet = true },
                        modifier = Modifier.offset(x = 9.dp, y = (-9).dp)
                    ) {
                        Icon(
                            Icons.Outlined.Tune, contentDescription = "Sort and Filter",
                            tint = DarkBlue, modifier = Modifier.size(30.dp)
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDropdown(
    options: List<DropdownOption>,
    label: String,
    selectedOption: DropdownOption,
    updateSelection: (newSelection: DropdownOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var leadingIcon: @Composable() (() -> Unit)? = null
    if (selectedOption.icon != null) {
        leadingIcon = @Composable { Icon(selectedOption.icon, selectedOption.text) }
    }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(), readOnly = true,
            value = selectedOption.text, onValueChange = {}, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = if (selectedOption.icon != null) leadingIcon else null,
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {selectionOption ->
                DropdownMenuItem(
                    text = {
                        Row {
                            if (selectionOption.icon != null) {
                                Icon(selectionOption.icon, selectionOption.text)
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                            Text(selectionOption.text, fontFamily = FontFamily.SansSerif, fontSize = 16.sp)
                        } },
                    onClick = { updateSelection(selectionOption); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    // TODO: This data should be provided with default values by container
    var searchQuery by remember { mutableStateOf("") }

    val sortOptions = listOf(DropdownOption("Newest Activities"), DropdownOption("Oldest Activities"))
    var selectedSort by remember { mutableStateOf( sortOptions[0]) }

    val filterOptions = listOf(
        DropdownOption("All"),
        DropdownOption("Jogging",Icons.Outlined.DirectionsRun),
        DropdownOption("Hiking", Icons.Outlined.Hiking),
        DropdownOption("Walking", Icons.Outlined.DirectionsWalk),
        DropdownOption("Swimming", Icons.Outlined.Pool),
        DropdownOption("Cycling", Icons.Outlined.DirectionsBike))
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column (
            modifier = Modifier
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                value = searchQuery, onValueChange = { newQuery -> searchQuery = newQuery },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search Icon") }
            )
            Spacer(modifier = Modifier.size(10.dp))
            BottomSheetDropdown(
                options = sortOptions, label = "Sort By",
                selectedOption = selectedSort,
                updateSelection = { newSelection -> selectedSort = newSelection }
            )
            Spacer(modifier = Modifier.size(10.dp))
            BottomSheetDropdown(
                options = filterOptions, label = "Filter Activity Type",
                selectedOption = selectedFilter,
                updateSelection = {newSelection -> selectedFilter = newSelection }
            )
            Spacer(modifier = Modifier.size(10.dp))
//          TODO: Apply button, which uses a callback to return the updated values to the main screen
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Apply", fontSize = 16.sp)
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