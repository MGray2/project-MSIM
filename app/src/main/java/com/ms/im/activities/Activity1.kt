package com.ms.im.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ms.im.MyApp
import com.ms.im.database.viewmodels.GroupViewModel
import com.ms.im.database.entities.Group
import com.ms.im.ui.components.*
import com.ms.im.ui.theme.IMTheme
import androidx.lifecycle.viewmodel.compose.viewModel

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
            // Local
            val context = LocalContext.current
            val app = LocalContext.current.applicationContext as MyApp
            val groupVM: GroupViewModel = viewModel(factory = app.groupViewModelFactory)
            val groups by groupVM.allGroups.collectAsState()
            val groupId = groupVM.selectedGroupId.collectAsState()
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
                        Text("Groups")
                        button.Generic({
                            newGroupName = ""
                            showCreateScreen = true },
                            "Create Group")

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

                        // Window to see groups
                        Column(modifier = Modifier.fillMaxWidth()) {
                            groups.forEach { group ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    button.Generic({}, group.name, modifier = Modifier.weight(1F))
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
