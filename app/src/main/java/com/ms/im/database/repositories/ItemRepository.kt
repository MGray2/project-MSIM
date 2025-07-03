package com.ms.im.database.repositories

import com.ms.im.database.daos.ItemDao
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val dao: ItemDao) {

    // Get Items from Group
    fun getItemsByGroup(groupId: Long): Flow<List<Item>> {
        return dao.getItemsByGroup(groupId)
    }

    // Get one instance by id
    suspend fun getById(id: Long): Item? {
        return dao.getItemById(id)
    }

    suspend fun getTemplatesByGroup(groupId: Long): List<Item> {
        return dao.getTemplatesByGroup(groupId)
    }

    suspend fun getInstancesByTemplate(templateId: Long): List<Item> {
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