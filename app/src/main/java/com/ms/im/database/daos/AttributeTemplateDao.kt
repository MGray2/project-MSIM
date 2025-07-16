package com.ms.im.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ms.im.database.entities.AttributeTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface AttributeTemplateDao {

    @Query("SELECT * FROM attribute_template_table WHERE itemId = :itemId")
    fun getAllByItem(itemId: Long): Flow<List<AttributeTemplate>>

    @Query("SELECT * FROM attribute_template_table WHERE itemId = :itemId")
    suspend fun getAllByItemNow(itemId: Long): List<AttributeTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attributeTemplate: AttributeTemplate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attributes: List<AttributeTemplate>)

    @Update
    suspend fun update(attributeTemplate: AttributeTemplate)

    @Delete
    suspend fun delete(attributeTemplate: AttributeTemplate)

    @Query("DELETE FROM attribute_template_table WHERE itemId = :itemId")
    suspend fun deleteAllByItem(itemId: Long)

    @Transaction
    suspend fun replaceAttributes(itemId: Long, newAttributes: List<AttributeTemplate>) {
        deleteAllByItem(itemId)
        insertAll(newAttributes)
    }
}