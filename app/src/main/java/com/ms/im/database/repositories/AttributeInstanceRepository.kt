package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeInstanceDao
import com.ms.im.database.entities.AttributeInstance

class AttributeInstanceRepository(private val dao: AttributeInstanceDao) {

    suspend fun getByItem(itemId: Long): List<AttributeInstance> {
        return dao.getInstancesForItem(itemId)
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

    suspend fun delete(instance: AttributeInstance) {
        dao.delete(instance)
    }

    suspend fun deleteAllForItem(itemId: Long) {
        dao.deleteAllForItem(itemId)
    }
}