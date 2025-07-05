package com.ms.im

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AttributeTemplateDraft(
    name: String = "",
    type: AttributeType = AttributeType.TEXT
) {
    var name by mutableStateOf(name)
    var type by mutableStateOf(type)
}
