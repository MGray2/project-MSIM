package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "item_table",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val groupId: Long, // id of Group
    val name: String, // Item Name
    val isTemplate: Boolean = false,
    val templateId: Long? = null // only non-null for instances
) : Parcelable
