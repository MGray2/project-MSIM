package com.ms.im.database.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ms.im.OrderDirection
import com.ms.im.SortField
import com.ms.im.SortOrder
import com.ms.im.database.daos.ItemDao
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val dao: ItemDao) {

    // Getters
    fun getPagedTemplatesFilteredSorted(
        query: String,
        groupId: Long,
        order: SortOrder,
        randomSeed: Int = 0
    ): Flow<PagingData<Item>> {
        val pagingSourceFactory: () -> PagingSource<Int, Item> = when (order) {
            is SortOrder.Field -> {
                when (order.field) {
                    SortField.Name -> {
                        when (order.direction) {
                            OrderDirection.Asc -> { { dao.searchTemplatesByNameAsc(query, groupId) } }
                            OrderDirection.Desc -> { { dao.searchTemplatesByNameDesc(query, groupId) } }
                        }
                    }
                }
            }
            is SortOrder.Random -> { { dao.searchTemplatesByRandom(query, groupId, randomSeed)} }
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