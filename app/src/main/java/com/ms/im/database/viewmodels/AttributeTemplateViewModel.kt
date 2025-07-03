package com.ms.im.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.repositories.AttributeTemplateRepository
import kotlinx.coroutines.launch

class AttributeTemplateViewModel(
    private val repository: AttributeTemplateRepository
) : ViewModel() {

    // Fetch all templates for a given item
    fun getTemplatesByItem(itemId: Long): LiveData<List<AttributeTemplate>> = liveData {
        emit(repository.getByItem(itemId))
    }

    // Insert a single template
    fun insert(template: AttributeTemplate) = viewModelScope.launch {
        repository.insert(template)
    }

    // Insert multiple templates (useful when cloning or creating blueprints)
    fun insertAll(templates: List<AttributeTemplate>) = viewModelScope.launch {
        repository.insertAll(templates)
    }

    // Update a template
    fun update(template: AttributeTemplate) = viewModelScope.launch {
        repository.update(template)
    }

    // Delete a template
    fun delete(template: AttributeTemplate) = viewModelScope.launch {
        repository.delete(template)
    }
}