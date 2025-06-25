package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ms.im.AttributeType

@Entity(
    tableName = "attribute_table",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Attribute(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String, // Attribute Name
    val itemId: Long, // Id of item
    val type: AttributeType, // text, tag, number, decimal, state
    // Storage for type
    val textValue: String? = null,
    val tagValue: String? = null,
    val numberValue: Long? = null,
    val decimalValue: Double? = null,
    val stateValue: Boolean? = null
)

