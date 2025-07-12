package com.ms.im

import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.Item

data class ItemRow(
    val item: Item,
    val attributes: Map<Long, AttributeInstance> // Template id, instance
)
