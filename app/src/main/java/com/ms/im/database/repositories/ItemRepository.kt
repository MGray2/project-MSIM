package com.ms.im.database.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ms.im.SortOrder
import com.ms.im.database.daos.ItemDao
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val dao: ItemDao) {

    // Getters
    fun getPagedTemplatesFiltered(
        query: String,
        groupId: Long,
        order: SortOrder,
        randomSeed: Int = 0
    ): Flow<PagingData<Item>> {
        val pagingSourceFactory = when (order) {
            SortOrder.NameAsc -> { { dao.searchTemplatesByNameAsc(query, groupId) } }
            SortOrder.NameDesc -> { { dao.searchTemplatesByNameDesc(query, groupId) } }
            SortOrder.IdAsc -> { { dao.searchTemplatesByIdAsc(query, groupId) } }
            SortOrder.IdDesc -> { { dao.searchTemplatesByIdDesc(query, groupId) } }
            SortOrder.Random -> { { dao.searchTemplatesByRandom(query, groupId, randomSeed) } }
        }

        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getItemsByGroup(groupId: Long): Flow<List<Item>> {
        return dao.getItemsByGroup(groupId)
    }

    suspend fun getById(id: Long): Item? {
        return dao.getItemById(id)
    }

    suspend fun getTemplatesByGroup(groupId: Long): List<Item> {
        return dao.getTemplatesByGroup(groupId)
    }

    fun getInstancesByTemplate(templateId: Long): Flow<List<Item>> {
        return dao.getInstancesByTemplate(templateId)
    }

    // Insert
    suspend fun insert(item: Item): Long {
        return dao.insert(item)
    }

    // Update
    suspend fun update(item: Item) {
        return dao.update(item)
    }

    // Delete
    suspend fun delete(item: Item) {
        return dao.delete(item)
    }
}