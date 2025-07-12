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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ms.im.MyApp
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.Group
import com.ms.im.database.entities.Item
import com.ms.im.database.viewmodels.AttributeInstanceViewModel
import com.ms.im.database.viewmodels.AttributeTemplateViewModel
import com.ms.im.database.viewmodels.ItemViewModel
import com.ms.im.ui.theme.IMTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.forEach

class Activity3 : ComponentActivity() {
    // Global

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

            val attributeTemplates by remember(selectedTemplate) {
                selectedTemplate?.let {
                    tempVM.getAllByItem(it.id)
                } ?: flowOf(emptyList())
            }.collectAsState(initial = emptyList())

            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top bar
                        Text("Groups > ${selectedGroup?.name} > ${selectedTemplate?.name}")

                        Box(modifier = Modifier.fillMaxSize()) {
                            Row {
                                attributeTemplates.forEach { attribute ->
                                    Text(attribute.name, modifier = Modifier.weight(1f))
                                }
                            }
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {

                            }
                        }
                    }
                }
            }
        }
    }
}