package com.ms.im.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ms.im.ui.components.Buttons
import com.ms.im.ui.theme.IMTheme

class Activity1 : ComponentActivity() {
    // Global
    private val button = Buttons()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Local
            val context = LocalContext.current
            IMTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    ) {
                        Text("Groups")
                        button.Generic({}, "Create Group")
                    }
                }
            }
        }
    }
}