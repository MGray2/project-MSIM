package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.manager.AttributeManager
import com.ms.im.database.repositories.AttributeInstanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AttributeInstanceViewModel(
    private val repository: AttributeInstanceRepository,
    private val attributeManager: AttributeManager
) : ViewModel() {

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