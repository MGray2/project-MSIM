package com.ms.im.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String // Group Name
) : Parcelable
