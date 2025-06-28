package com.ms.im.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class Inputs {

    @Composable
    fun Field(value: String, onValueChange: (String) -> Unit, placeholder: String) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun Field(value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardOptions: KeyboardOptions) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions
        )
    }
}