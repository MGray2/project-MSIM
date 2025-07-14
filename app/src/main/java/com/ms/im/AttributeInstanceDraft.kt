package com.ms.im

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AttributeInstanceDraft(
    val templateId: Long = 0L,
    val type: AttributeType,
    valueText: String = "",
    valueNumber: Long = 0L,
    valueDecimal: Double = 0.0,
    valueBool: Boolean = false
) {
    var valueText by mutableStateOf(valueText)
    var valueNumber by mutableLongStateOf(valueNumber)
    var valueDecimal by mutableDoubleStateOf(valueDecimal)
    var valueBool by mutableStateOf(valueBool)
}
