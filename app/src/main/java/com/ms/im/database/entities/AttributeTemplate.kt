package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ms.im.AttributeType
import java.util.UUID

@Entity(
    tableName = "attribute_template_table",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AttributeTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val itemId: Long, // relates to the blueprint item
    val stableId: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AttributeType,
    val position: Int
)
