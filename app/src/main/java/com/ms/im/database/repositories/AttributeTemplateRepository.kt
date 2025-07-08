package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeTemplateDao
import com.ms.im.database.entities.AttributeTemplate
import kotlinx.coroutines.flow.Flow

class AttributeTemplateRepository(private val dao: AttributeTemplateDao) {

    fun getByItem(itemId: Long): Flow<List<AttributeTemplate>> {
        return dao.getTemplatesByItem(itemId)
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

    suspend fun delete(attribute: AttributeTemplate) {
        dao.delete(attribute)
    }

    suspend fun deleteAllForItem(itemId: Long) {
        dao.deleteAllForItem(itemId)
    }

    suspend fun replaceAttributes(itemId: Long, newAttributes: List<AttributeTemplate>) {
        dao.deleteAllForItem(itemId)
        dao.insertAll(newAttributes)

    }
}