package com.ms.im.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ms.im.MyApp
import com.ms.im.database.viewmodels.GroupViewModel
import com.ms.im.database.entities.Group
import com.ms.im.ui.components.*
import com.ms.im.ui.theme.IMTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ms.im.OrderDirection
import com.ms.im.SortField
import com.ms.im.SortOrder
import kotlinx.coroutines.launch

// Groups Screen
class Activity1 : ComponentActivity() {
    // Global
    private val button = Buttons()
    private val screen = Screens()
    private val input = Inputs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Database
            val app = LocalContext.current.applicationContext as MyApp
            val groupVM: GroupViewModel = viewModel(factory = app.groupViewModelFactory)
            val groups = groupVM.pagedGroups.collectAsLazyPagingItems()
            val groupId by groupVM.selectedGroupId.collectAsState()
            val searchText = groupVM.searchQuery.collectAsState()
            val sortOrder by groupVM.sortOrder.collectAsState()

            // Local
            val context = LocalContext.current
            var showCreateScreen by remember { mutableStateOf(false) }
            var showUpdateScreen by remember { mutableStateOf(false) }
            var showDeleteScreen by remember { mutableStateOf(false) }
            var showSortScreen by remember { mutableStateOf(false) }
            var enableUpdate by remember { mutableStateOf(false) }
            var enableDelete by remember { mutableStateOf(false) }
            var newGroupName by remember { mutableStateOf("")}
            val scope = rememberCoroutineScope()


            // Setup
            LaunchedEffect(groupId) {
                if (groupId != null) {
                    enableUpdate = true
                    enableDelete = true
                } else {
                    enableUpdate = false
                    enableDelete = false
                }
            }

            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top Bar
                        Text("Groups")

                        // Row of buttons for Create/Update/Delete
                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            // Create Button
                            button.Generic({
                                newGroupName = ""
                                showCreateScreen = true },
                                "Create")

                            // Update Button
                            button.Generic({
                                groupId?.let { id ->
                                    scope.launch {
                                        val groupName = groupVM.getById(id)?.name ?: return@launch
                                        newGroupName = groupName
                                        showUpdateScreen = true
                                    }
                                }
                            }, placeholder = "Edit", enabled = enableUpdate)

                            // Delete Button
                            button.Generic({
                                groupId?.let { id ->
                                    scope.launch {
                                        val groupName = groupVM.getById(id)?.name ?: return@launch
                                        newGroupName = groupName
                                        showDeleteScreen = true
                                    }
                                }
                            }, placeholder = "Delete", enabled = enableDelete)
                        }

                        // Search Bar
                        input.Field(searchText.value, { groupVM.setSearchQuery(it) }, "Search")

                        // Sort Button
//                        button.Cycle(
//                            options = SortOrder.entries,
//                            selected = sortOrder,
//                            onOptionChange = { groupVM.setSortOrder(it) },
//                            labelMapper = { order ->
//                                when (order) {
//                                    SortOrder.NameAsc -> "Name ↑"
//                                    SortOrder.NameDesc -> "Name ↓"
//                                    SortOrder.IdAsc -> "ID ↑"
//                                    SortOrder.IdDesc -> "ID ↓"
//                                    SortOrder.Random -> "Random"
//                                } }
//                        )
                        button.Generic({ showSortScreen = true }, "Sort")
                        Box(modifier = Modifier.fillMaxSize()) {

                            // Window to see groups
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(groups.itemCount) { index ->
                                    val group = groups[index]
                                    if (group != null) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            // Interactable Button for each Group iterable
                                            ItemButton(
                                                onClick = { // Single click to select Group
                                                    groupVM.selectGroup(group.id)
                                                    // click again to unselect
                                                    if (groupId == group.id) groupVM.resetSelectedGroup()
                                                },
                                                onDoubleClick = { // Double click to go to Activity 2
                                                    val intent = Intent(context, Activity2::class.java)
                                                    intent.putExtra("selectedGroup", group)
                                                    context.startActivity(intent)
                                                },
                                                group.name
                                            )
                                        }
                                    }
                                }
                                // Loading more results
                                if (groups.loadState.append is LoadState.Loading ||
                                    groups.loadState.refresh is LoadState.Loading
                                ) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                // Failure
                                if (groups.loadState.refresh is LoadState.Error) {
                                    val error = (groups.loadState.refresh as LoadState.Error).error
                                    item {
                                        Text(
                                            text = "Error loading results: ${error.message}",
                                            color = Color.Red,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                                // Empty results
                                if (groups.loadState.refresh is LoadState.NotLoading && groups.itemCount == 0) {
                                    item {
                                        Text(
                                            text = "No results.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            // Sort Mini-screen
                            SortMenu(
                                visible = showSortScreen,
                                onDismiss = { showSortScreen = false },
                                sortOrder = SortOrder.Field(SortField.Name, OrderDirection.Asc),
                                onApply = { field, direction ->
                                    groupVM.setSortOrder(SortOrder.Field(field, direction))
                                },
                                onRandom = {
                                    groupVM.setSortOrder(SortOrder.Random)
                                }
                            )

                            // Create Group Mini-screen
                            CreateGroupScreen(
                                visible = showCreateScreen,
                                onDismiss = { showCreateScreen = false },
                                onSubmit = {
                                    val newGroup = Group(name = newGroupName)
                                    newGroupName = ""
                                    showCreateScreen = false
                                    groupVM.insert(newGroup)},
                                groupName = newGroupName,
                                onGroupNameChange = { newGroupName = it }
                            )

                            // Update Group Mini-screen
                            UpdateGroupScreen(
                                visible = showUpdateScreen,
                                onDismiss = { showUpdateScreen = false },
                                onSubmit = {
                                    groupId?.let { id ->
                                        groupVM.updateById(id, newGroupName)
                                        groupVM.resetSelectedGroup()
                                    }
                                    newGroupName = ""
                                    showUpdateScreen = false
                                },
                                groupName = newGroupName,
                                onGroupNameChange = { newGroupName = it }
                            )

                            // Delete Group Mini-screen
                            DeleteGroupScreen(
                                visible = showDeleteScreen,
                                onDismiss = { showDeleteScreen = false },
                                onSubmit = {
                                    groupId?.let { id ->
                                        groupVM.deleteById(id)
                                        groupVM.resetSelectedGroup()
                                    }
                                    newGroupName = ""
                                    showDeleteScreen = false
                                },
                                groupName = newGroupName
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SortMenu(
        visible: Boolean,
        onDismiss: () -> Unit,
        sortOrder: SortOrder,
        onApply: (SortField, OrderDirection) -> Unit,
        onRandom: () -> Unit
    ) {
        var selectedField by remember { mutableStateOf(
            (sortOrder as? SortOrder.Field)?.field ?: SortField.Name
        ) }

        var selectedDirection by remember { mutableStateOf(
            (sortOrder as? SortOrder.Field)?.direction ?: OrderDirection.Asc
        ) }

        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Sort",
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sort by:")
                    SortFieldRadio(
                        current = selectedField,
                        field = SortField.Name,
                        label = "Name",
                        onSelect = { selectedField = it }
                    )

                    Text("Direction:")
                    SortDirectionRadio(
                        current = selectedDirection,
                        direction = OrderDirection.Asc,
                        label = "Ascending",
                        onSelect = { selectedDirection = it }
                    )
                    SortDirectionRadio(
                        current = selectedDirection,
                        direction = OrderDirection.Desc,
                        label = "Descending",
                        onSelect = { selectedDirection = it }
                    )

                    button.Generic(
                        onClick = {
                            onRandom()
                            onDismiss()
                        },
                        placeholder = "Random Sort"
                    )
                }
            },
            confirmButton = {
                button.Generic(
                    onClick = {
                        onApply(selectedField, selectedDirection)
                        onDismiss()
                    },
                    placeholder = "Start"
                )
            },
            cancelButton = {
                button.Generic(onDismiss, "Cancel")
            }
        )
    }

    // Temp
    @Composable
    fun SortFieldRadio(
        current: SortField,
        field: SortField,
        label: String,
        onSelect: (SortField) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = current == field,
                onClick = { onSelect(field) }
            )
            Text(label)
        }
    }

    // Temp
    @Composable
    fun SortDirectionRadio(
        current: OrderDirection,
        direction: OrderDirection,
        label: String,
        onSelect: (OrderDirection) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = current == direction,
                onClick = { onSelect(direction) }
            )
            Text(label)
        }
    }

    // Temp
    @Composable
    fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Text(label, fontSize = 18.sp)
        }
    }

    @Composable
    private fun ItemButton(
        onClick: () -> Unit,
        onDoubleClick: () -> Unit,
        placeholder: String
    ) {
        button.ItemInteractable(
            modifier = Modifier,
            onClick = onClick,
            onDoubleClick = onDoubleClick,
            content = {
                Text(placeholder)
            }
        )
    }

    @Composable
    private fun CreateGroupScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: () -> Unit,
        groupName: String,
        onGroupNameChange: (String) -> Unit
    ) {
        // Pop-up window form to create a Group
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Create Group",
            content = {
                input.Field(groupName, onGroupNameChange, "Group name")
            },
            confirmButton = { button.Generic(onSubmit, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }

    @Composable
    private fun UpdateGroupScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: () -> Unit,
        groupName: String,
        onGroupNameChange: (String) -> Unit
    ) {
        // Pop-up window form to change a Group
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Update Group",
            content = {
                input.Field(groupName, onGroupNameChange, "Group name")
            },
            confirmButton = { button.Generic(onSubmit, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }

    @Composable
    private fun DeleteGroupScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: () -> Unit,
        groupName: String,
    ) {
        // Pop-up window form to delete a Group
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Delete Group",
            content = {
                Text("Are you sure you want to delete $groupName?")
            },
            confirmButton = { button.Generic(onSubmit, "Confirm") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }
}
