package com.ms.im.database.manager

import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.ms.im.database.AppDatabase
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.repositories.AttributeInstanceRepository
import com.ms.im.database.repositories.AttributeTemplateRepository
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AttributeManager(
    private val templateRepository: AttributeTemplateRepository,
    private val instanceRepository: AttributeInstanceRepository,
    private val itemRepository: ItemRepository,
    private val database: AppDatabase
) {

    // Operation to replace templates and instances, then auto-populate with old information
    suspend fun updateTemplatesAndBackFillInstances(itemId: Long, newTemplates: List<AttributeTemplate>) {
        database.withTransaction {
            // 1. Replace templates
            templateRepository.replaceAttributes(itemId, newTemplates)

            // 2. Fetch updated templates to get their IDs (if needed)
            val updatedTemplates = templateRepository.getAllByItem(itemId).first()

            // 3. Get all instances for this item/template
            val instances = itemRepository.getInstancesByTemplate(itemId).first()

            // 4. For each instance, back-fill missing attribute instances
            instances.forEach { instance ->
                val existingInstances = instanceRepository.getAllByItem(instance.id).first()
                val existingTemplateIds = existingInstances.map { it.templateId }.toSet()

                val missingTemplates = updatedTemplates.filter { it.id !in existingTemplateIds }

                val newAttributeInstances = missingTemplates.map { template ->
                    AttributeInstance(
                        itemId = instance.id,
                        templateId = template.id,
                        valueText = null,
                        valueNumber = null,
                        valueDecimal = null,
                        valueBool = null
                    )
                }

                instanceRepository.insertAll(newAttributeInstances)
            }
        }
    }
}