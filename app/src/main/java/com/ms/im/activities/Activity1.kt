package com.ms.im.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            var showScreen by remember { mutableStateOf(false) }
            var newGroupName by remember { mutableStateOf("")}
            val app = LocalContext.current.applicationContext as MyApp
            val groupVM: GroupViewModel = viewModel(factory = app.groupViewModelFactory)
            val groups by groupVM.allGroups.collectAsState()
            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        Text("Groups")
                        button.Generic({ showScreen = true }, "Create Group")

                        // Window to see groups
                        Column(modifier = Modifier.fillMaxWidth()) {
                            groups.forEach { group ->
                                button.Generic({}, "${group.id} ${group.name}")
                            }
                        }

                        screen.MiniScreen(
                            visible = showScreen,
                            onDismiss = { showScreen = false},
                            title = "Create Group",
                            content = {
                                input.Field(newGroupName, { newGroupName = it}, "Group name")
                            },
                            confirmButton = { button.Generic({
                                val newGroup = Group(name = newGroupName)
                                newGroupName = ""
                                showScreen = false
                                groupVM.insert(newGroup) }, "Save")},
                            cancelButton = { button.Generic({ showScreen = false }, "Cancel") })

                    }
                }
            }
        }
    }

}
