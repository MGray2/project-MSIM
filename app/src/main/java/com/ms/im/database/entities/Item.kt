package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_table",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val groupId: Long, // id of Group
    val name: String // Item Name
)
