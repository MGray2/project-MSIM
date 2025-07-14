package com.ms.im.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.repositories.AttributeInstanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AttributeInstanceViewModel(
    private val repository: AttributeInstanceRepository
) : ViewModel() {

    // Get one specific attribute value for a given item/template combination
    fun getAttributeValue(itemId: Long, templateId: Long): LiveData<AttributeInstance?> = liveData {
        emit(repository.getByItemAndTemplate(itemId, templateId))
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

    fun delete(instance: AttributeInstance) = viewModelScope.launch {
        repository.delete(instance)
    }

    fun deleteAllForItem(itemId: Long) = viewModelScope.launch {
        repository.deleteAllForItem(itemId)
    }
}