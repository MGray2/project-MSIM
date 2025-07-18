package com.ms.im

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

class AttributeTemplateDraft(
    name: String = "",
    type: AttributeType = AttributeType.TEXT,
    val stableId: String = UUID.randomUUID().toString() // preserve if update/edit
) {
    var name by mutableStateOf(name)
    var type by mutableStateOf(type)
}
