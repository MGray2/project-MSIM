package com.ms.im.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String // Group Name
)
