package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeDao
import com.ms.im.database.entities.Attribute
import kotlinx.coroutines.flow.Flow

class AttributeRepository(private val dao: AttributeDao) {

    // Get Attributes from Item
    fun getAttributesByItem(itemId: Long): Flow<List<Attribute>> {
        return dao.getAttributesByItem(itemId)
    }

    // Get one instance by id
    suspend fun getById(id: Long): Attribute? {
        return dao.getAttributeById(id)
    }

    // Insert
    suspend fun insert(attribute: Attribute): Long {
        return dao.insert(attribute)
    }

    // Update
    suspend fun update(attribute: Attribute) {
        dao.update(attribute)
    }

    // Delete
    suspend fun delete(attribute: Attribute) {
        dao.delete(attribute)
    }
}