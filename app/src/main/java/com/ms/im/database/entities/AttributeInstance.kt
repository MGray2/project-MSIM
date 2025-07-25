package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ms.im.AttributeType

@Entity(
    tableName = "attribute_instance_table",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AttributeTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttributeInstance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val itemId: Long, // the instance item this belongs to
    val templateId: Long, // the attribute it's referencing
    val valueText: String? = null,
    val valueNumber: Long? = null,
    val valueDecimal: Double? = null,
    val valueBool: Boolean? = null
) {
    fun displayValue(type: AttributeType): String {
        return when (type) {
            AttributeType.TEXT -> valueText ?: "---"
            AttributeType.TAG -> valueText ?: "---"
            AttributeType.NUMBER -> valueNumber?.toString() ?: "---"
            AttributeType.DECIMAL -> valueDecimal?.toString() ?: "---"
            AttributeType.STATE -> valueBool?.toString() ?: "---"
        }
    }
}
