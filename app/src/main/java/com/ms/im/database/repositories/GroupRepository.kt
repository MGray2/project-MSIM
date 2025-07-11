package com.ms.im.database.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ms.im.SortOrder
import com.ms.im.database.daos.GroupDao
import com.ms.im.database.entities.Group
import kotlinx.coroutines.flow.Flow

class GroupRepository(private val dao: GroupDao) {

    // Getters
    val allGroups: Flow<List<Group>> = dao.getAllGroups()

    suspend fun getById(id: Long): Group? {
        return dao.getGroupById(id)
    }

    fun getPagedGroupsFiltered(query: String): Flow<PagingData<Group>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { dao.searchPagedGroups(query) }
        ).flow
    }

    fun getPagedGroupsFilteredSorted(
        query: String,
        order: SortOrder,
        randomSeed: Int = 0
    ): Flow<PagingData<Group>> {
        val pagingSourceFactory = when (order) {
            SortOrder.NameAsc -> { -> dao.searchGroupsByNameAsc(query) }
            SortOrder.NameDesc -> { -> dao.searchGroupsByNameDesc(query) }
            SortOrder.IdAsc -> { -> dao.searchGroupsByIdAsc(query) }
            SortOrder.IdDesc -> { -> dao.searchGroupsByIdDesc(query) }
            SortOrder.Random -> { -> dao.searchGroupsByRandom(query, randomSeed) }
        }

        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    // Insert
    suspend fun insert(group: Group): Long {
        return dao.insert(group)
    }

    // Update
    suspend fun update(group: Group) {
        dao.update(group)
    }

    // Delete
    suspend fun delete(group: Group) {
        dao.delete(group)
    }
}