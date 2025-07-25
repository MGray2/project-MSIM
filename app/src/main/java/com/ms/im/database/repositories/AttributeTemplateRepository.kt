package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeTemplateDao
import com.ms.im.database.entities.AttributeTemplate
import kotlinx.coroutines.flow.Flow

class AttributeTemplateRepository(private val dao: AttributeTemplateDao) {

    fun getAllByItem(itemId: Long): Flow<List<AttributeTemplate>> {
        return dao.getAllByItem(itemId)
    }

    suspend fun insert(attribute: AttributeTemplate): Long {
        return dao.insert(attribute)
    }

    suspend fun insertAll(attributes: List<AttributeTemplate>) {
        dao.insertAll(attributes)
    }

    suspend fun update(attribute: AttributeTemplate) {
        dao.update(attribute)
    }

    suspend fun updateAll(attributes: List<AttributeTemplate>) {
        dao.updateAll(attributes)
    }

    suspend fun delete(attribute: AttributeTemplate) {
        dao.delete(attribute)
    }

    suspend fun deleteAll(attributes: List<AttributeTemplate>) {
        dao.deleteAll(attributes)
    }

    suspend fun deleteAllForItem(itemId: Long) {
        dao.deleteAllByItem(itemId)
    }

    suspend fun replaceAttributes(itemId: Long, newAttributes: List<AttributeTemplate>) {
        dao.replaceAttributes(itemId, newAttributes)
    }
}