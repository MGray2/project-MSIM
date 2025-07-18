package com.ms.im.database.manager

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.ms.im.database.AppDatabase
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.repositories.AttributeInstanceRepository
import com.ms.im.database.repositories.AttributeTemplateRepository
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.first

class AttributeManager(
    private val templateRepository: AttributeTemplateRepository,
    private val instanceRepository: AttributeInstanceRepository,
    private val itemRepository: ItemRepository,
    private val database: AppDatabase
) {

    suspend fun updateTemplatesAndBackFillInstances(itemId: Long, newTemplates: List<AttributeTemplate>) {
        database.withTransaction {
            // normalize and assign positions
            val normalizedNewTemplates = newTemplates
                .sortedBy { it.position }
                .mapIndexed { index, attr -> attr.copy(position = index, itemId = itemId) }

            // get existing templates before deletion
            val oldTemplates = templateRepository.getAllByItem(itemId).first()

            // get all existing instance values by (itemId + template.stableId)
            val instancesByItem = mutableMapOf<Long, Map<String, AttributeInstance>>()
            val items = itemRepository.getInstancesByTemplate(itemId).first()

            for (item in items) {
                val instances = instanceRepository.getAllByItem(item.id).first()
                val instanceMap = mutableMapOf<String, AttributeInstance>()

                for (instance in instances) {
                    val oldTemplate = oldTemplates.find { it.id == instance.templateId }
                    if (oldTemplate != null) {
                        instanceMap[oldTemplate.stableId] = instance
                    }
                }

                instancesByItem[item.id] = instanceMap
            }

            // replace templates
            templateRepository.replaceAttributes(itemId, normalizedNewTemplates)

            // re-obtain templates with new IDs
            val updatedTemplates = templateRepository.getAllByItem(itemId).first()
            val templateMap = updatedTemplates.associateBy { it.stableId }

            // for each item instance, insert new AttributeInstances
            for (item in items) {
                val oldInstanceMap = instancesByItem[item.id] ?: emptyMap()

                val newInstances = templateMap.mapNotNull { (stableId, newTemplate) ->
                    val oldInstance = oldInstanceMap[stableId]
                    AttributeInstance(
                        itemId = item.id,
                        templateId = newTemplate.id,
                        valueText = oldInstance?.valueText,
                        valueNumber = oldInstance?.valueNumber,
                        valueDecimal = oldInstance?.valueDecimal,
                        valueBool = oldInstance?.valueBool
                    )
                }

                instanceRepository.deleteAllByItem(item.id)
                instanceRepository.insertAll(newInstances)
            }
        }
    }
}