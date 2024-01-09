package com.foxden.fitnessapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foxden.fitnessapp.Routes.ADD_ACTIVITY_FORM_SCREEN
import com.foxden.fitnessapp.data.ActivityLog
import com.foxden.fitnessapp.data.ActivityLogDAO
import com.foxden.fitnessapp.data.ActivityType
import com.foxden.fitnessapp.data.ActivityTypeDAO
import com.foxden.fitnessapp.data.Constants
import com.foxden.fitnessapp.data.DBHelper
import com.foxden.fitnessapp.ui.components.ActivityWidget
import com.foxden.fitnessapp.ui.components.NavBar

class DropdownOption(val id: Int, val text: String, val icon: ImageVector? = null)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ActivityJournalScreen(navigation: NavController, dbHelper: DBHelper) {
    val activityTypeList = remember { ActivityTypeDAO.fetchAll(dbHelper.writableDatabase).toMutableStateList() }

    val sortOptions = listOf(DropdownOption(0, "Newest Activities"), DropdownOption(1, "Oldest Activities"))
    val filterOptions = remember { mutableStateListOf<DropdownOption>() }
    LaunchedEffect(Unit) {
        for (activityType: ActivityType in activityTypeList) {
            filterOptions.add(DropdownOption(
                activityType.id,
                activityType.name,
                Constants.ActivityIcons.values()[activityType.iconId].image
            ))
        }
    }

    var searchQuery: String by remember { mutableStateOf("") }
    var selectedSort: DropdownOption by remember { mutableStateOf( sortOptions[0] ) }
    var selectedFilter: DropdownOption? by remember { mutableStateOf( null ) }

    var showSheet by remember { mutableStateOf(false) }
    if (showSheet) { BottomSheet(
        searchQuery, { newQuery -> searchQuery = newQuery },
        selectedSort, { newSort -> selectedSort = newSort },
        selectedFilter, { newFilter -> selectedFilter = newFilter },
        sortOptions, filterOptions) { showSheet = false } }

    val activityLogs = remember { mutableStateListOf<ActivityLog>() }
    val sortedActivityLogs = remember { mutableStateListOf<ActivityLog>() }

    fun sortActivities() {
        var activities: List<ActivityLog> = activityLogs

        if (searchQuery != "") {
            activities = activities.filter { log -> log.title.contains(searchQuery) || log.notes.contains(searchQuery) }
        }

        if (selectedFilter != null) {
            activities = activities.filter { log -> log.activityTypeId == selectedFilter!!.id }
        }

        activities = if (selectedSort.id == 0) {
            activities.sortedByDescending { log -> log.startTime }
        } else {
            activities.sortedBy { log -> log.startTime }
        }

        sortedActivityLogs.clear()
        for (log: ActivityLog in activities) {
            sortedActivityLogs.add(log)
        }
    }

    LaunchedEffect(Unit) {
        for (a in ActivityLogDAO.fetchAll(dbHelper.readableDatabase)) {
            activityLogs.add(a)
            sortedActivityLogs.add(a)
        }
        sortActivities()
    }

    LaunchedEffect(key1 = searchQuery, key2 = selectedSort.text, key3 = selectedFilter?.text) {
        sortActivities()
    }

    // remove activities
    var selectedActivity by remember { mutableStateOf<ActivityLog?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Activity") },
            text = { Text("Are you sure you want to delete this activity?") },
            confirmButton = { TextButton(onClick = {
                showDialog = false
                selectedActivity?.let { activity ->
                    activityLogs.remove(activity)
                    ActivityLogDAO.delete(dbHelper.writableDatabase, activity)
                }
            }) { Text("Yes") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        bottomBar = { NavBar(navigation = navigation) }
    ) { scaffoldPaddingValues ->
        Column(modifier = Modifier.padding(scaffoldPaddingValues)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 25.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Activity Journal", fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Row {
                        IconButton(
                            onClick = { navigation.navigate(ADD_ACTIVITY_FORM_SCREEN) },
                            modifier = Modifier.offset(x = 9.dp, y = (-11).dp)
                        ) {
                            Icon(
                                Icons.Outlined.Add, contentDescription = "Add Activity Manually",
                                tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(30.dp)
                            )
                        }

                        IconButton(
                            onClick = { showSheet = true },
                            modifier = Modifier.offset(x = 9.dp, y = (-11).dp)
                        ) {
                            Icon(
                                Icons.Outlined.Tune, contentDescription = "Sort and Filter",
                                tint = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                if (activityLogs.size == 0) {
                    Text(text = "No activities logged", color = MaterialTheme.colorScheme.onSecondary)
                }
                
                LazyColumn {
                    items(sortedActivityLogs) { activityLog ->
                        ActivityWidget(
                            activityLog, activityType = activityTypeList.first { it.id == activityLog.activityTypeId},
                            modifier = Modifier.pointerInput(activityLog) {
                                detectTapGestures(onLongPress = {
                                    selectedActivity = activityLog
                                    showDialog = true
                                })
                            })
                            Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDropdown(
    options: List<DropdownOption>,
    label: String,
    selectedOption: DropdownOption?,
    updateSelection: (newSelection: DropdownOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    var leadingIcon: @Composable (() -> Unit)? = null
    if (selectedOption?.icon != null) {
        leadingIcon = @Composable { Icon(selectedOption.icon, selectedOption.text) }
    }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(), readOnly = true,
            value = selectedOption?.text ?: "", onValueChange = {}, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = if (selectedOption?.icon != null) leadingIcon else null
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
fun BottomSheet(
    searchQuery: String, updateSearchQuery: (new: String) -> Unit,
    selectedSort: DropdownOption, updateSelectedSort: (new: DropdownOption) -> Unit,
    selectedFilter: DropdownOption?, updateSelectedFilter: (new: DropdownOption) -> Unit,
    sortOptions: List<DropdownOption>,
    filterOptions: List<DropdownOption>,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

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
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                value = searchQuery, onValueChange = { newQuery -> updateSearchQuery(newQuery) },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(
                    Icons.Outlined.Search, contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onSecondary
                ) }
            )
            Spacer(modifier = Modifier.size(10.dp))
            BottomSheetDropdown(
                options = sortOptions, label = "Sort By",
                selectedOption = selectedSort,
                updateSelection = { newSelection -> updateSelectedSort(newSelection) }
            )
            Spacer(modifier = Modifier.size(10.dp))
            BottomSheetDropdown(
                options = filterOptions, label = "Filter Activity Type",
                selectedOption = selectedFilter,
                updateSelection = {newSelection -> updateSelectedFilter(newSelection) }
            )
        }
    }
}