package com.ms.im.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ms.im.database.entities.AttributeInstance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributeInstanceDao {
    @Query("SELECT * FROM attribute_instance_table WHERE itemId = :itemId")
    fun getAllByItem(itemId: Long): Flow<List<AttributeInstance>>

    @Query("SELECT * FROM attribute_instance_table WHERE itemId = :itemId AND templateId = :templateAttrId")
    suspend fun getValueForAttribute(itemId: Long, templateAttrId: Long): AttributeInstance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attributeInstance: AttributeInstance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instances: List<AttributeInstance>)

    @Update
    suspend fun update(attributeInstance: AttributeInstance)

    @Delete
    suspend fun delete(attributeInstance: AttributeInstance)

    @Query("DELETE FROM attribute_instance_table WHERE itemId = :itemId")
    suspend fun deleteAllForItem(itemId: Long)
}