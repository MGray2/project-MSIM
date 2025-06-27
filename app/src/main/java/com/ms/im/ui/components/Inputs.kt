package com.ms.im.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

class Inputs {

    @Composable
    fun Field(value: String, onValueChange: (String) -> Unit, placeholder: String) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) }
        )
    }
}