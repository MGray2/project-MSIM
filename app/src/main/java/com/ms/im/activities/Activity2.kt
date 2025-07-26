package com.ms.im.activities

import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ms.im.AttributeTemplateDraft
import com.ms.im.AttributeType
import com.ms.im.MyApp
import com.ms.im.SortOrder
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
            val searchText = itemVM.searchQuery.collectAsState()
            val sortOrder by itemVM.sortOrder.collectAsState()
            val selectedGroupId by itemVM.selectedGroupId.collectAsState()
            val selectedTemplateId by itemVM.selectedTemplateId.collectAsState()
            val templates = itemVM.pagedTemplates.collectAsLazyPagingItems()

            // Local
            val selectedGroup = intent.getParcelableExtra("selectedGroup", Group::class.java)

            var showCreateScreen by remember { mutableStateOf(false) }
            var showUpdateScreen by remember { mutableStateOf(false) }
            var showDeleteScreen by remember { mutableStateOf(false) }
            var enableUpdate by remember { mutableStateOf(false) }
            var enableDelete by remember { mutableStateOf(false) }
            var itemName by remember { mutableStateOf("") }
            val attributeDrafts = remember { mutableStateListOf<AttributeTemplateDraft>() }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            // Setup
            itemVM.resetSelectedGroupId()
            itemVM.setSelectedGroupId(selectedGroup?.id)

            LaunchedEffect(selectedTemplateId) {
                selectedTemplateId?.let { id ->
                    val item = itemVM.getById(id)
                    tempVM.getAllByItem(id).collect { attributes ->
                        if (item != null) {
                            itemName = item.name
                            attributeDrafts.clear()
                            attributeDrafts.addAll(
                                attributes.map {
                                    AttributeTemplateDraft(
                                        name = it.name,
                                        type = it.type,
                                        stableId = it.stableId
                                    )
                                }
                            )
                        }
                    }
                }
            }

            LaunchedEffect(selectedTemplateId) {
                if (selectedTemplateId != null) {
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
                        Text("Groups > ${selectedGroup?.name}")
                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            // Create Button
                            button.Generic({
                                showCreateScreen = true
                                itemName = "" },
                                "Create")

                            // Update Button
                            button.Generic({
                                showUpdateScreen = true
                            }, placeholder = "Edit", enabled = enableUpdate)

                            // Delete Button
                            button.Generic({
                                showDeleteScreen = true
                            }, placeholder = "Delete", enabled = enableDelete)
                        }

                        // Search Bar
                        input.Field(searchText.value, { itemVM.setSearchQuery(it) }, "Search")

                        // Sort Button
//                        button.Cycle(
//                            options = SortOrder.entries,
//                            selected = sortOrder,
//                            onOptionChange = { itemVM.setSortOrder(it) },
//                            labelMapper = { order ->
//                                when (order) {
//                                    SortOrder.NameAsc -> "Name ↑"
//                                    SortOrder.NameDesc -> "Name ↓"
//                                    SortOrder.IdAsc -> "ID ↑"
//                                    SortOrder.IdDesc -> "ID ↓"
//                                    SortOrder.Random -> "Random"
//                                } }
//                        )

                        Box(modifier = Modifier.fillMaxSize()) {

                            // Window to see Items
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(templates.itemCount) { index ->
                                    val template = templates[index]
                                    if (template != null) {
                                        Row {
                                            ItemButton( onClick = { // Single click to select template item
                                                itemVM.setSelectedTemplateId(template.id)
                                                // click again to unselect
                                                if (selectedTemplateId == template.id) itemVM.resetSelectedTemplateId()
                                            },
                                                onDoubleClick = { // Double click to go to Activity 3
                                                val intent = Intent(context, Activity3::class.java)
                                                intent.putExtra("selectedTemplate", template)
                                                intent.putExtra("selectedGroup", selectedGroup)
                                                startActivity(intent)
                                            }, template.name)
                                        }
                                    }

                                }
                                // Loading more results
                                if (templates.loadState.append is LoadState.Loading ||
                                    templates.loadState.refresh is LoadState.Loading
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
                                if (templates.loadState.refresh is LoadState.Error) {
                                    val error = (templates.loadState.refresh as LoadState.Error).error
                                    item {
                                        Text(
                                            text = "Error loading results: ${error.message}",
                                            color = Color.Red,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                                // Empty results
                                if (templates.loadState.refresh is LoadState.NotLoading && templates.itemCount == 0) {
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

                            // Create Item Template Screen
                            CreateTemplateScreen(
                                visible = showCreateScreen,
                                onDismiss = { showCreateScreen = false },
                                onSubmit = { name, drafts ->
                                    selectedGroupId?.let { id ->
                                        scope.launch {
                                            val itemId = itemVM.insertReturn(
                                                Item(
                                                    name = name,
                                                    isTemplate = true,
                                                    groupId = id
                                                )
                                            )
                                            val attributes = drafts.mapIndexed { index, draft ->
                                                AttributeTemplate(
                                                    name = draft.name,
                                                    type = draft.type,
                                                    stableId = draft.stableId,
                                                    itemId = itemId,
                                                    position = index
                                                )
                                            }
                                            tempVM.insertAll(attributes)
                                        }
                                    }
                                    // Reset
                                    showCreateScreen = false
                                    itemName = ""
                                    attributeDrafts.clear()

                                },
                                templateName = itemName,
                                onTemplateNameChange = { itemName = it },
                                attributeDrafts = attributeDrafts
                            )

                            // Update Item Template Screen
                            UpdateTemplateScreen(
                                visible = showUpdateScreen,
                                onDismiss = { showUpdateScreen = false },
                                onSubmit = { templateId, drafts ->
                                    if (templateId == null) return@UpdateTemplateScreen

                                    scope.launch {
                                        val item = itemVM.getById(templateId) ?: return@launch
                                        itemVM.update(item.copy(name = itemName))

                                        val newAttributes = drafts.mapIndexed { index, draft ->
                                            AttributeTemplate(
                                                name = draft.name,
                                                type = draft.type,
                                                itemId = item.id,
                                                stableId = draft.stableId,
                                                position = index
                                            )
                                        }

                                        tempVM.updateTemplatesAndBackFill(item.id, newAttributes)
                                        // Reset
                                        showUpdateScreen = false
                                        itemName = ""
                                        attributeDrafts.clear()
                                        itemVM.resetSelectedTemplateId()
                                    }
                                },
                                templateId = selectedTemplateId,
                                templateName = itemName,
                                onTemplateNameChange = { itemName = it},
                                attributeDrafts = attributeDrafts
                            )

                            // Delete Item Template Screen
                            DeleteTemplateScreen(
                                visible = showDeleteScreen,
                                onDismiss = { showDeleteScreen = false },
                                onSubmit = { templateId ->
                                    templateId?.let {
                                        itemVM.deleteById(templateId)
                                    }
                                    showDeleteScreen = false
                                    itemVM.resetSelectedTemplateId()
                                    itemName = ""
                                },
                                templateId = selectedTemplateId,
                                templateName = itemName
                            )
                        }
                    }
                }
            }
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
            confirmButton = { button.Generic( {onSubmit(templateName, attributeDrafts.toList())}, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") })
    }

    @Composable
    fun UpdateTemplateScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (Long?, List<AttributeTemplateDraft>) -> Unit,
        templateId: Long?,
        templateName: String,
        onTemplateNameChange: (String) -> Unit,
        attributeDrafts: SnapshotStateList<AttributeTemplateDraft>
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Edit Item Design",
            content = {
                input.Field(templateName, onTemplateNameChange, "Item Name")
                attributeDrafts.forEachIndexed { index, draft ->
                    AttributeDraftField(
                        draft = draft,
                        onRemove = { attributeDrafts.removeAt(index) }
                    )
                }
                button.Generic({ attributeDrafts.add(AttributeTemplateDraft()) }, "Add Attribute")
                      },
            confirmButton = { button.Generic({ onSubmit(templateId, attributeDrafts.toList()) }, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") }
        )
    }

    @Composable
    fun DeleteTemplateScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (Long?) -> Unit,
        templateId: Long?,
        templateName: String,
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Delete Item Design",
            content = {
                Text("Are you sure you want to delete $templateName?")
            },
            confirmButton = { button.Generic({ onSubmit(templateId) }, "Confirm") },
            cancelButton = { button.Generic(onDismiss, "Cancel") }
        )
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