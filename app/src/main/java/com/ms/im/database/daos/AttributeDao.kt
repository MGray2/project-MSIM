package com.ms.im.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ms.im.database.entities.Attribute
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attribute: Attribute): Long

    @Update
    suspend fun update(attribute: Attribute)

    @Delete
    suspend fun delete(attribute: Attribute)

    @Query("SELECT * FROM attribute_table WHERE itemId = :itemId ORDER BY name ASC")
    fun getAttributesByItem(itemId: Long): Flow<List<Attribute>>

    @Query("SELECT * FROM attribute_table WHERE id = :id LIMIT 1")
    suspend fun getAttributeById(id: Long): Attribute?
}