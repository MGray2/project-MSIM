package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.repositories.AttributeInstanceRepository
import com.ms.im.database.repositories.AttributeTemplateRepository
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AttributeInstanceViewModel(
    private val repository: AttributeInstanceRepository,
    private val itemRepository: ItemRepository,
    private val templateRepository: AttributeTemplateRepository
) : ViewModel() {

    suspend fun backFillAllInstancesByTemplate(
        templateId: Long,
        templateAttributes: List<AttributeTemplate>
    ) {
        val instances = itemRepository.getInstancesByTemplate(templateId).first()

        for (item in instances) {
            val existingInstances = repository.getAllByItem(item.id).first()
            val existingTemplateIds = existingInstances.map { it.templateId }.toSet()

            val missingTemplates = templateAttributes.filter { it.id !in existingTemplateIds }

            val newInstances = missingTemplates.map { template ->
                AttributeInstance(
                    itemId = item.id,
                    templateId = template.id,
                    valueText = null,
                    valueNumber = null,
                    valueDecimal = null,
                    valueBool = null
                )
            }

            repository.insertAll(newInstances)
        }
    }

    fun updateTemplateAndBackFill(itemId: Long, newTemplates: List<AttributeTemplate>) = viewModelScope.launch {
        val instanceItems = itemRepository.getInstancesByTemplate(itemId).first()

        templateRepository.updateTemplatesAndBackFillInstances(
            itemId = itemId,
            newTemplates = newTemplates,
            instanceItems = instanceItems
        )
    }

    // Get all attribute instances for a given item
    fun getAllByItem(itemId: Long): Flow<List<AttributeInstance>> = repository.getAllByItem(itemId)

    // Insert or update a single value
    fun insert(instance: AttributeInstance) = viewModelScope.launch {
        repository.insert(instance) // Replace strategy handles update if ID matches
    }

    fun insertAll(instances: List<AttributeInstance>) = viewModelScope.launch {
        repository.insertAll(instances)
    }

    fun update(instance: AttributeInstance) = viewModelScope.launch {
        repository.update(instance)
    }

    // Coroutine independent
    fun replaceAttributes(itemId: Long, newAttributes: List<AttributeInstance>) = viewModelScope.launch {
        repository.replaceAttributes(itemId, newAttributes)
    }

    // Use within coroutine scope
    suspend fun replaceAttributesNow(itemId: Long, newAttributes: List<AttributeInstance>) {
        repository.replaceAttributes(itemId, newAttributes)
    }

    fun delete(instance: AttributeInstance) = viewModelScope.launch {
        repository.delete(instance)
    }

    fun deleteAllByItem(itemId: Long) = viewModelScope.launch {
        repository.deleteAllByItem(itemId)
    }
}