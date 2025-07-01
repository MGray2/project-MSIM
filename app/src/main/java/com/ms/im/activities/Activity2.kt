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
import com.ms.im.ui.components.Buttons
import com.ms.im.ui.components.Inputs
import com.ms.im.ui.components.Screens
import com.ms.im.ui.theme.IMTheme

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
            // Local
            val group = intent.getParcelableExtra("selectedGroup", Group::class.java)
            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        // Top Bar
                        Text("Groups > ${group?.name}")
                    }
                }
            }
        }
    }
}