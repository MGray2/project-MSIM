package com.ms.im.database.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ms.im.database.repositories.AttributeInstanceRepository
import com.ms.im.database.viewmodels.AttributeInstanceViewModel

class AttributeInstanceFactory(
    private val repository: AttributeInstanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttributeInstanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttributeInstanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}