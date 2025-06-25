package com.ms.im.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM item_table WHERE groupId = :groupId ORDER BY name ASC")
    fun getItemsByGroup(groupId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item_table WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): Item?
}