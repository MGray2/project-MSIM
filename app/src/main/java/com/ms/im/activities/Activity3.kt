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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

            val attributeDrafts = remember { mutableStateListOf<AttributeInstanceDraft>() }
            var showCreateScreen by remember { mutableStateOf(false) }
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


            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top bar
                        Text("Groups > ${selectedGroup?.name} > ${selectedTemplate?.name}")

                        button.Generic({ showCreateScreen = true }, "Create Instance")

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
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        attributeTemplates.forEach { template ->
                                            val value = attrMap[template.id]?.displayValue(template.type) ?: ""
                                            Text(value, modifier = Modifier.weight(1f))
                                        }
                                    }
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
                                        attributeDrafts.clear()
                                        showCreateScreen = false
                                    }

                                },
                                attributeTemplates = attributeTemplates,
                                attributeDrafts = attributeDrafts
                            )
                        }
                    }
                }
            }
        }
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
                    input.Field(
                        value = draft.valueNumber.toString(),
                        onValueChange = {
                            draft.valueNumber = it.toLongOrNull()?: 0L
                        },
                        placeholder = "Enter number"
                    )
                }
                AttributeType.DECIMAL -> {
                    input.Field(
                        value = draft.valueDecimal.toString(),
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