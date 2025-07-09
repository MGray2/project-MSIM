package com.ms.im.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ms.im.database.entities.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: Group): Long

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun delete(group: Group)

    // Getters
    @Query("SELECT * FROM group_table ORDER BY name ASC")
    fun getAllGroups(): Flow<List<Group>>

    @Query("SELECT * FROM group_table WHERE id = :id LIMIT 1")
    suspend fun getGroupById(id: Long): Group?

    // Search functions
    @Query("SELECT * FROM group_table WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPagedGroups(query: String): PagingSource<Int, Group>

    @Query("SELECT * FROM group_table WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchGroupsByNameAsc(query: String): PagingSource<Int, Group>

    @Query("SELECT * FROM group_table WHERE name LIKE '%' || :query || '%' ORDER BY name DESC")
    fun searchGroupsByNameDesc(query: String): PagingSource<Int, Group>

    @Query("SELECT * FROM group_table WHERE name LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchGroupsByIdAsc(query: String): PagingSource<Int, Group>

    @Query("SELECT * FROM group_table WHERE name LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchGroupsByIdDesc(query: String): PagingSource<Int, Group>

    @Query("""
    SELECT * FROM group_table 
    WHERE name LIKE '%' || :query || '%' 
    ORDER BY (ABS(id * :seed) % 10000)
""")
    fun searchGroupsByRandom(query: String, seed: Int): PagingSource<Int, Group>


}