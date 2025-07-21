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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
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
import com.ms.im.AttributeInstanceDraft
import com.ms.im.AttributeType
import com.ms.im.MyApp
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.entities.Group
import com.ms.im.database.entities.Item
import com.ms.im.database.viewmodels.AttributeInstanceViewModel
import com.ms.im.database.viewmodels.AttributeTemplateViewModel
import com.ms.im.database.viewmodels.ItemViewModel
import com.ms.im.ui.components.Buttons
import com.ms.im.ui.components.Inputs
import com.ms.im.ui.components.Screens
import com.ms.im.ui.theme.IMTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class Activity3 : ComponentActivity() {
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
            val instVM: AttributeInstanceViewModel = viewModel(factory = app.attributeInstanceFactory)


            // Local
            val selectedTemplate = intent.getParcelableExtra("selectedTemplate", Item::class.java)
            val selectedGroup = intent.getParcelableExtra("selectedGroup", Group::class.java)

            var selectedInstanceId by remember { mutableStateOf<Long?>(null) }
            val attributeDrafts = remember { mutableStateListOf<AttributeInstanceDraft>() }
            var showCreateScreen by remember { mutableStateOf(false) }
            var showUpdateScreen by remember { mutableStateOf(false) }
            var showDeleteScreen by remember { mutableStateOf(false) }
            var enableUpdate by remember { mutableStateOf(false) }
            var enableDelete by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Setup
            val attributeTemplates by remember(selectedTemplate) {
                selectedTemplate?.let {
                    tempVM.getAllByItem(it.id)
                } ?: flowOf(emptyList())
            }.collectAsState(initial = emptyList())

            val itemInstances by remember(selectedTemplate) {
                selectedTemplate?.let {
                    itemVM.getInstancesByTemplate(it.id)
                } ?: flowOf(emptyList())
            }.collectAsState(initial = emptyList())

            val attributeInstances by remember(selectedInstanceId) {
                selectedInstanceId?.let { id ->
                    instVM.getAllByItem(id)
                } ?: flowOf(emptyList())
            }.collectAsState(initial = emptyList())


            LaunchedEffect(showCreateScreen) {
                if (showCreateScreen && attributeDrafts.isEmpty()) {
                    attributeDrafts.clear()
                    attributeDrafts.addAll(
                        attributeTemplates.map {
                            AttributeInstanceDraft(
                                templateId = it.id,
                                type = it.type
                            )
                        }
                    )
                }
            }

            LaunchedEffect(showUpdateScreen, selectedInstanceId) {
                if (showUpdateScreen && selectedInstanceId != null) {
                    val id = selectedInstanceId!!
                    val item = itemVM.getById(id)
                    val instances = instVM.getAllByItem(id).first()
                    val templates = selectedTemplate
                        ?.let { tempVM.getAllByItem(it.id).first() }
                        ?: emptyList()
                    if (item != null && instances.isNotEmpty()) {
                        attributeDrafts.clear()

                        attributeDrafts.addAll(
                            instances.mapNotNull { instance ->
                                val type = templates.find { it.id == instance.templateId }?.type
                                if (type != null) {
                                    AttributeInstanceDraft(
                                        templateId = instance.templateId,
                                        type = type,
                                        valueText = instance.valueText ?: "",
                                        valueNumber = instance.valueNumber ?: 0L,
                                        valueDecimal = instance.valueDecimal ?: 0.0,
                                        valueBool = instance.valueBool ?: false
                                    )
                                } else null // skip if template not found
                            }
                        )
                    }
                }
            }

            LaunchedEffect(selectedInstanceId) {
                if (selectedInstanceId != null) {
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
                        // Top bar
                        Text("Groups > ${selectedGroup?.name} > ${selectedTemplate?.name}")

                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            // Create Button
                            button.Generic({ showCreateScreen = true }, "Create")

                            button.Generic({ showUpdateScreen = true },
                                placeholder = "Edit", enabled = enableUpdate)

                            button.Generic({ showDeleteScreen = true },
                                placeholder = "Delete", enabled = enableDelete)
                        }


                        // Header row to display template attributes
                        Row {
                            attributeTemplates.forEach { attribute ->
                                Text(attribute.name, modifier = Modifier.weight(1f))
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(items = itemInstances) { item ->
                                    // Collect attribute instances for this item
                                    val attrInstances by instVM.getAllByItem(item.id)
                                        .collectAsState(initial = emptyList())

                                    // Convert to map
                                    val attrMap = attrInstances.associateBy { it.templateId }

                                    // Display row
                                    ItemButton(
                                        attributeTemplates = attributeTemplates,
                                        attrMap = attrMap,
                                        onClick = {
                                            selectedInstanceId = item.id
                                        },
                                        onDoubleClick = {/* nothing for now */})
                                }
                            }

                            // Create Instance Screen
                            CreateInstanceScreen(
                                visible = showCreateScreen,
                                onDismiss = { showCreateScreen = false },
                                onSubmit = { drafts ->
                                    val templateId = selectedTemplate?.id
                                    val groupId = selectedGroup?.id

                                    if (templateId == null || groupId == null) return@CreateInstanceScreen
                                    scope.launch {
                                        val instanceId = itemVM.insertReturn(
                                            Item(
                                                name = selectedTemplate.name,
                                                groupId = groupId,
                                                isTemplate = false,
                                                templateId = templateId
                                            )
                                        )

                                        val instances = drafts.map { draft ->
                                            AttributeInstance(
                                                itemId = instanceId,
                                                templateId = draft.templateId,
                                                valueText = if (draft.type == AttributeType.TEXT || draft.type == AttributeType.TAG) draft.valueText else null,
                                                valueNumber = if (draft.type == AttributeType.NUMBER) draft.valueNumber else null,
                                                valueDecimal = if (draft.type == AttributeType.DECIMAL) draft.valueDecimal else null,
                                                valueBool = if (draft.type == AttributeType.STATE) draft.valueBool else null
                                            )
                                        }
                                        instVM.insertAll(instances)
                                        // Clear
                                        attributeDrafts.clear()
                                        showCreateScreen = false
                                    }

                                },
                                attributeTemplates = attributeTemplates,
                                attributeDrafts = attributeDrafts
                            )

                            // Update Instance Screen
                            UpdateInstanceScreen(
                                visible = showUpdateScreen,
                                onDismiss = { showUpdateScreen = false },
                                onSubmit = { itemId, drafts ->
                                    itemId?.let { id ->
                                        scope.launch {

                                            val newAttributes = drafts.map { draft ->
                                                AttributeInstance(
                                                    itemId = id,
                                                    templateId = draft.templateId,
                                                    valueText = if (draft.type == AttributeType.TEXT || draft.type == AttributeType.TAG) draft.valueText else null,
                                                    valueNumber = if (draft.type == AttributeType.NUMBER) draft.valueNumber else null,
                                                    valueDecimal = if (draft.type == AttributeType.DECIMAL) draft.valueDecimal else null,
                                                    valueBool = if (draft.type == AttributeType.STATE) draft.valueBool else null
                                                )
                                            }

                                            instVM.replaceAttributes(id, newAttributes)
                                            // Clear
                                            showUpdateScreen = false
                                            attributeDrafts.clear()
                                        }
                                    }
                                },
                                itemId = selectedInstanceId,
                                attributeTemplates = attributeTemplates,
                                attributeDrafts = attributeDrafts
                            )

                            // Delete Instance Screen
                            DeleteInstanceScreen(
                                visible = showDeleteScreen,
                                onDismiss = { showDeleteScreen = false },
                                onSubmit = { id ->
                                    if (id == null) return@DeleteInstanceScreen
                                    itemVM.deleteById(id)
                                    // Clear
                                    showDeleteScreen = false
                                    selectedInstanceId = null
                                },
                                instanceId = selectedInstanceId,
                                attributeInstances = attributeInstances,
                                attributeTemplates = attributeTemplates
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ItemButton(
        attributeTemplates: List<AttributeTemplate>,
        attrMap: Map<Long, AttributeInstance>,
        onClick: () -> Unit,
        onDoubleClick: () -> Unit
    ) {
        button.ItemInteractable(
            onClick = onClick,
            onDoubleClick = onDoubleClick,
            content = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    attributeTemplates.forEach { template ->
                        val value = attrMap[template.id]?.displayValue(template.type) ?: ""
                        Text(
                            text = value,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        )
    }

    @Composable
    fun CreateInstanceScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (List<AttributeInstanceDraft>) -> Unit,
        attributeTemplates: List<AttributeTemplate>,
        attributeDrafts: SnapshotStateList<AttributeInstanceDraft>
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Create Instance",
            content = {
                if (attributeDrafts.isNotEmpty()) {
                    attributeTemplates.forEachIndexed { index, template ->
                        AttributeInstanceField(
                            draft = attributeDrafts[index],
                            template = template
                        )
                    }
                } else {
                    Text("Loading fields...")
                }

            },
            confirmButton = { button.Generic({ onSubmit(attributeDrafts.toList()) }, "Save") },
            cancelButton = { button.Generic( { onDismiss() }, "Cancel")}
        )
    }

    @Composable
    fun UpdateInstanceScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (Long?, List<AttributeInstanceDraft>) -> Unit,
        itemId: Long?,
        attributeTemplates: List<AttributeTemplate>,
        attributeDrafts: SnapshotStateList<AttributeInstanceDraft>
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Edit Instance",
            content = {
                if (itemId == null) return@MiniScreen
                attributeTemplates.zip(attributeDrafts).forEach { (template, draft) ->
                    AttributeInstanceField(
                        draft = draft,
                        template = template
                    )
                }
            },
            confirmButton = { button.Generic({ onSubmit(itemId, attributeDrafts) }, "Save") },
            cancelButton = { button.Generic(onDismiss, "Cancel") }
        )
    }

    @Composable
    fun DeleteInstanceScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        onSubmit: (Long?) -> Unit,
        instanceId: Long?,  // Add instance id here
        attributeInstances: List<AttributeInstance>, // or pass the full data
        attributeTemplates: List<AttributeTemplate> // To show attribute names (headers)
    ) {
        screen.MiniScreen(
            visible = visible,
            onDismiss = onDismiss,
            title = "Delete Instance",
            content = {
                Text("Are you sure you want to delete this instance?")
                Row {
                    attributeTemplates.forEach { template ->
                        Text(
                            text = template.name,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Row {
                    attributeTemplates.forEach { template ->
                        val attributeInstance = attributeInstances.find { it.templateId == template.id }

                        Text(
                            text = when (template.type) {
                                AttributeType.TEXT, AttributeType.TAG -> attributeInstance?.valueText ?: "---"
                                AttributeType.NUMBER -> attributeInstance?.valueNumber?.toString() ?: "---"
                                AttributeType.DECIMAL -> attributeInstance?.valueDecimal?.toString() ?: "---"
                                AttributeType.STATE -> attributeInstance?.valueBool?.toString() ?: "---"
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = { button.Generic({ onSubmit(instanceId) }, "Confirm")},
            cancelButton = { button.Generic(onDismiss, "Cancel")}

        )
    }

    @Composable
    fun AttributeInstanceField(
        draft: AttributeInstanceDraft,
        template: AttributeTemplate
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(template.name)

            when (template.type) {
                AttributeType.TEXT, AttributeType.TAG -> {
                    input.Field(
                        value = draft.valueText,
                        onValueChange = { draft.valueText = it },
                        placeholder = "Enter text"
                    )
                }
                AttributeType.NUMBER -> {
                    val textValue = if (draft.valueNumber == 0L) "" else draft.valueNumber.toString()
                    input.Field(
                        value = textValue,
                        onValueChange = {
                            draft.valueNumber = it.toLongOrNull()?: 0L
                        },
                        placeholder = "Enter number"
                    )
                }
                AttributeType.DECIMAL -> {
                    val textValue = if (draft.valueDecimal == 0.0) "" else draft.valueDecimal.toString()
                    input.Field(
                        value = textValue,
                        onValueChange = {
                            draft.valueDecimal = it.toDoubleOrNull()?: 0.0
                        },
                        placeholder = "Enter decimal"
                    )
                }
                AttributeType.STATE -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = draft.valueBool,
                            onCheckedChange = { draft.valueBool = it }
                        )
                        Text("Enabled")
                    }
                }
            }
        }
    }
}