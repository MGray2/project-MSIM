package com.ms.im.database.daos

import androidx.paging.PagingSource
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

    @Query("SELECT * FROM item_table WHERE groupId = :groupId AND isTemplate = 1")
    suspend fun getTemplatesByGroup(groupId: Long): List<Item>

    @Query("SELECT * FROM item_table WHERE templateId = :templateId AND isTemplate = 0")
    suspend fun getInstancesByTemplate(templateId: Long): List<Item>

    @Query("""
        SELECT * FROM item_table 
        WHERE isTemplate = 1 AND groupId = :groupId AND name LIKE '%' || :query || '%' 
        ORDER BY name ASC
    """)
    fun searchTemplatesByNameAsc(query: String, groupId: Long): PagingSource<Int, Item>

    @Query("""
        SELECT * FROM item_table 
        WHERE isTemplate = 1 AND groupId = :groupId AND name LIKE '%' || :query || '%' 
        ORDER BY name DESC
    """)
    fun searchTemplatesByNameDesc(query: String, groupId: Long): PagingSource<Int, Item>

    @Query("""
        SELECT * FROM item_table 
        WHERE isTemplate = 1 AND groupId = :groupId AND name LIKE '%' || :query || '%' 
        ORDER BY id ASC
    """)
    fun searchTemplatesByIdAsc(query: String, groupId: Long): PagingSource<Int, Item>

    @Query("""
        SELECT * FROM item_table 
        WHERE isTemplate = 1 AND groupId = :groupId AND name LIKE '%' || :query || '%' 
        ORDER BY id DESC
    """)
    fun searchTemplatesByIdDesc(query: String, groupId: Long): PagingSource<Int, Item>

    @Query("""
        SELECT * FROM item_table 
        WHERE isTemplate = 1 AND groupId = :groupId AND name LIKE '%' || :query || '%' 
        ORDER BY (ABS(id * :seed) % 10000)
    """)
    fun searchTemplatesByRandom(query: String, groupId: Long, seed: Int): PagingSource<Int, Item>
}