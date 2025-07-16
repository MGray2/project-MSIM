package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeInstanceDao
import com.ms.im.database.entities.AttributeInstance
import kotlinx.coroutines.flow.Flow

class AttributeInstanceRepository(private val dao: AttributeInstanceDao) {

    fun getAllByItem(itemId: Long): Flow<List<AttributeInstance>> {
        return dao.getAllByItem(itemId)
    }

    suspend fun getByItemAndTemplate(itemId: Long, templateId: Long): AttributeInstance? {
        return dao.getValueForAttribute(itemId, templateId)
    }

    suspend fun insert(instance: AttributeInstance): Long {
        return dao.insert(instance)
    }

    suspend fun insertAll(instances: List<AttributeInstance>) {
        dao.insertAll(instances)
    }

    suspend fun update(instance: AttributeInstance) {
        dao.update(instance)
    }

    suspend fun replaceAttributes(itemId: Long, newAttributes: List<AttributeInstance>) {
        dao.replaceAttributes(itemId, newAttributes)
    }

    suspend fun delete(instance: AttributeInstance) {
        dao.delete(instance)
    }

    suspend fun deleteAllByItem(itemId: Long) {
        dao.deleteAllByItem(itemId)
    }
}