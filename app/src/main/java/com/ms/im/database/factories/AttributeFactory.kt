package com.ms.im.database.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ms.im.database.repositories.AttributeRepository
import com.ms.im.database.viewmodels.AttributeViewModel

class AttributeFactory(
    private val repository: AttributeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttributeViewModel::class.java)) {
            return AttributeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}