package com.ms.im.database.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ms.im.database.manager.AttributeManager
import com.ms.im.database.repositories.AttributeTemplateRepository
import com.ms.im.database.viewmodels.AttributeTemplateViewModel

class AttributeTemplateFactory(
    private val repository: AttributeTemplateRepository,
    private val attributeManager: AttributeManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttributeTemplateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttributeTemplateViewModel(repository, attributeManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}