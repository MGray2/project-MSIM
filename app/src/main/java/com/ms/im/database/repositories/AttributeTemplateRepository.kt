package com.ms.im.database.repositories

import androidx.room.Transaction
import com.ms.im.database.daos.AttributeInstanceDao
import com.ms.im.database.daos.AttributeTemplateDao
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AttributeTemplateRepository(
    private val dao: AttributeTemplateDao,
    private val instanceDao: AttributeInstanceDao
) {

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

    suspend fun delete(attribute: AttributeTemplate) {
        dao.delete(attribute)
    }

    suspend fun deleteAllForItem(itemId: Long) {
        dao.deleteAllByItem(itemId)
    }

    suspend fun replaceAttributes(itemId: Long, newAttributes: List<AttributeTemplate>) {
        dao.replaceAttributes(itemId, newAttributes)
    }

    @Transaction
    suspend fun updateTemplatesAndBackFillInstances(
        itemId: Long,
        newTemplates: List<AttributeTemplate>,
        instanceItems: List<Item>
    ) {
        // Step 1: Replace all existing attribute templates
        dao.deleteAllByItem(itemId)
        dao.insertAll(newTemplates)

        // Step 2: Refetch newly inserted templates with updated IDs
        val updatedTemplates = dao.getAllByItem(itemId).first()

        // Step 3: For each instance item, find what's missing and insert
        instanceItems.forEach { item ->
            val existingInstances = instanceDao.getAllByItem(item.id).first()
            val existingTemplateIds = existingInstances.map { it.templateId }.toSet()

            val missingTemplates = updatedTemplates.filter { it.id !in existingTemplateIds }

            val backFillInstances = missingTemplates.map { template ->
                AttributeInstance(
                    itemId = item.id,
                    templateId = template.id,
                    valueText = null,
                    valueNumber = null,
                    valueDecimal = null,
                    valueBool = null
                )
            }

            instanceDao.insertAll(backFillInstances)
        }
    }
}