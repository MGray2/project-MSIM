package com.ms.im.database.repositories

import com.ms.im.database.daos.AttributeTemplateDao
import com.ms.im.database.entities.AttributeTemplate

class AttributeTemplateRepository(private val dao: AttributeTemplateDao) {

    suspend fun getByItem(itemId: Long): List<AttributeTemplate> {
        return dao.getAttributesForTemplate(itemId)
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
}