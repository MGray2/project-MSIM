package com.ms.im.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.repositories.AttributeTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AttributeTemplateViewModel(
    private val repository: AttributeTemplateRepository
) : ViewModel() {

    // Fetch all templates for a given item
    fun getTemplatesByItem(itemId: Long): Flow<List<AttributeTemplate>> = repository.getByItem(itemId)


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

    fun replaceAttributes(itemId: Long, newAttributes: List<AttributeTemplate>) = viewModelScope.launch {
            repository.replaceAttributes(itemId, newAttributes)
        }


    // Delete a template
    fun delete(template: AttributeTemplate) = viewModelScope.launch {
        repository.delete(template)
    }
}