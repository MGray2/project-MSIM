package com.ms.im.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Screens {

    @Composable
    fun MiniScreen(
        visible: Boolean,
        onDismiss: () -> Unit,
        title: String,
        content: @Composable () -> Unit,
        confirmButton: @Composable (() -> Unit)? = null,
        cancelButton: @Composable (() -> Unit)? = null
    ) {
        if (!visible) return

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = true, onClick = {}) // Prevents interaction with behind elements
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                shape = RoundedCornerShape(0.dp),
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))

                    content() // Additional content will be here

                    Spacer(Modifier.height(16.dp))

                    // Bottom buttons
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        cancelButton?.invoke()
                        Spacer(Modifier.width(8.dp))
                        confirmButton?.invoke()
                    }
                }
            }
        }
    }
}