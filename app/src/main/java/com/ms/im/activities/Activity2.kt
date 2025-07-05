package com.ms.im.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ms.im.AttributeTemplateDraft
import com.ms.im.AttributeType
import com.ms.im.MyApp
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.entities.Group
import com.ms.im.database.entities.Item
import com.ms.im.database.viewmodels.AttributeTemplateViewModel
import com.ms.im.database.viewmodels.ItemViewModel
import com.ms.im.ui.components.Buttons
import com.ms.im.ui.components.Inputs
import com.ms.im.ui.components.Screens
import com.ms.im.ui.theme.IMTheme
import kotlinx.coroutines.launch

// Item Model Screen
class Activity2 : ComponentActivity() {
    // Global
    private val button = Buttons()
    private val screen = Screens()
    private val input = Inputs()


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Database
            val app = LocalContext.current.applicationContext as MyApp
            val itemVM: ItemViewModel = viewModel(factory = app.itemViewModelFactory)
            val tempVM: AttributeTemplateViewModel = viewModel(factory = app.attributeTemplateFactory)

            // Local
            val group = intent.getParcelableExtra("selectedGroup", Group::class.java)

            val groupId = itemVM.selectedGroupId
            val templates = itemVM.pagedTemplates.collectAsLazyPagingItems()
            var showCreateScreen by remember { mutableStateOf(false) }
            var itemName by remember { mutableStateOf("") }
            val attributeDrafts = remember { mutableStateListOf<AttributeTemplateDraft>() }
            val scope = rememberCoroutineScope()

            // Setup
            itemVM.resetSelectedGroupId()
            itemVM.setSelectedGroupId(group?.id)

            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top Bar
                        Text("Groups > ${group?.name}")
                        button.Generic({ showCreateScreen = true }, "Create Item")

                        Box(modifier = Modifier.fillMaxSize()) {
                            // Window to see Items
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(templates.itemCount) { index ->
                                    val template = templates[index]
                                    if (template != null) {
                                        button.Generic({}, template.name)
                                    }

                                }
                            }
                            CreateTemplateScreen(
                                visible = showCreateScreen,
                                onDismiss = { showCreateScreen = false },
                                onSubmit = { name, drafts ->
                                    groupId.value?.let { id ->
                                        scope.launch {
                                            val itemId = itemVM.insertReturn(
                                                Item(
                                                    name = name,
                                                    isTemplate = true,
                                                    groupId = id
                                                )
                                            )
                                            val attributes = drafts.map { draft ->
                                                AttributeTemplate(
                                                    name = draft.name,
                                                    type = draft.type,
                                                    itemId = itemId
                                                )
                                            }
                                            tempVM.insertAll(attributes)
                                        }
                                    }
                                    showCreateScreen = false
                                    itemName = ""
                                    attributeDrafts.clear()

                                },
                                templateName = itemName,
                                onTemplateNameChange = { itemName = it },
                                attributeDrafts = attributeDrafts
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CreateTemplateScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (String, List<AttributeTemplateDraft>) -> Unit,
        templateName: String,
        onTemplateNameChange: (String) -> Unit,
        attributeDrafts: SnapshotStateList<AttributeTemplateDraft>
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Create Item Design",
            content = {
                input.Field(templateName, onTemplateNameChange, "Item Name")
                attributeDrafts.forEachIndexed { index, draft ->
                    AttributeDraftField(
                        draft = draft,
                        onRemove = { attributeDrafts.removeAt(index) }
                    )
                }
                button.Generic({ attributeDrafts.add(AttributeTemplateDraft() )}, "Add Attribute")
                      },
            confirmButton = { button.Generic({onSubmit(templateName, attributeDrafts.toList())}, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }

    @Composable
    fun AttributeDraftField(
        draft: AttributeTemplateDraft,
        onRemove: () -> Unit
    ) {
        Column {
            input.Field(
                value = draft.name,
                onValueChange = { draft.name = it },
                placeholder = "Attribute name"
            )

            button.Cycle(
                options = AttributeType.entries,
                selected = draft.type,
                onOptionChange = { draft.type = it },
            )
            button.Generic(onRemove, "Remove Attribute")
        }
    }
}