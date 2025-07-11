package com.ms.im.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.ms.im.database.entities.Group
import com.ms.im.database.entities.Item
import com.ms.im.ui.theme.IMTheme

class Activity3 : ComponentActivity() {
    // Global
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Local
            val selectedTemplate = intent.getParcelableExtra("selectedTemplate", Item::class.java)
            val selectedGroup = intent.getParcelableExtra("selectedGroup", Group::class.java)
            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top bar
                        Text("Groups > ${selectedGroup?.name} > ${selectedTemplate?.name}")
                    }
                }
            }
        }
    }
}