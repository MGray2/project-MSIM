package com.ms.im.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ms.im.MyApp
import com.ms.im.database.viewmodels.GroupViewModel
import com.ms.im.database.entities.Group
import com.ms.im.ui.components.*
import com.ms.im.ui.theme.IMTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ms.im.SortOrder

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
            val groupId = groupVM.selectedGroupId.collectAsState()
            val searchText = groupVM.searchQuery.collectAsState()
            val sortOrder by groupVM.sortOrder.collectAsState()

            // Local
            val context = LocalContext.current
            var showCreateScreen by remember { mutableStateOf(false) }
            var showUpdateScreen by remember { mutableStateOf(false) }
            var showDeleteScreen by remember { mutableStateOf(false) }
            var newGroupName by remember { mutableStateOf("")}

            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top Bar
                        Text("Groups")
                        button.Generic({
                            newGroupName = ""
                            showCreateScreen = true },
                            "Create Group")
                        // Search Bar
                        input.Field(searchText.value, { groupVM.setSearchQuery(it) }, "Search")

                        // Sort Button
                        button.Cycle(
                            options = SortOrder.entries,
                            selected = sortOrder,
                            onOptionChange = { groupVM.setSortOrder(it) },
                            labelMapper = { order ->
                                when (order) {
                                    SortOrder.NameAsc -> "Name ↑"
                                    SortOrder.NameDesc -> "Name ↓"
                                    SortOrder.IdAsc -> "ID ↑"
                                    SortOrder.IdDesc -> "ID ↓"
                                    SortOrder.Random -> "Random"
                                } }
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Window to see groups
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(groups.itemCount) { index ->
                                    val group = groups[index]
                                    if (group != null) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            button.Generic({
                                                val intent = Intent(context, Activity2::class.java)
                                                intent.putExtra("selectedGroup", group)
                                                context.startActivity(intent)
                                            }, group.name, modifier = Modifier.weight(1F))
                                            button.Icon({
                                                newGroupName = group.name
                                                showUpdateScreen = true
                                                groupVM.selectGroup(group.id) },
                                                Icons.Filled.Edit)
                                            button.Icon({
                                                newGroupName = group.name
                                                showDeleteScreen = true
                                                groupVM.selectGroup(group.id) },
                                                Icons.Filled.Delete)
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
                                    groupId.value?.let { id ->
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
                                    groupId.value?.let { id ->
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
    fun CreateGroupScreen(
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
    fun UpdateGroupScreen(
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
            title = "Update Group",
            content = {
                input.Field(groupName, onGroupNameChange, "Group name")
            },
            confirmButton = { button.Generic(onSubmit, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }

    @Composable
    fun DeleteGroupScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: () -> Unit,
        groupName: String,
    ) {
        // Pop-up window form to create a Group
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
