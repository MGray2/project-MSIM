package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.Attribute
import com.ms.im.database.repositories.AttributeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AttributeViewModel(private val repository: AttributeRepository) : ViewModel() {

    private val _selectedItemId = MutableStateFlow<Long?>(null)
    private val _attributesForItem = MutableStateFlow<List<Attribute>>(emptyList())
    val attributesForItem: StateFlow<List<Attribute>> = _attributesForItem.asStateFlow()

    fun selectItem(itemId: Long) {
        _selectedItemId.value = itemId
    }

    fun insert(attribute: Attribute) = viewModelScope.launch {
        repository.insert(attribute)
    }

    fun update(attribute: Attribute) = viewModelScope.launch {
        repository.update(attribute)
    }

    fun delete(attribute: Attribute) = viewModelScope.launch {
        repository.delete(attribute)
    }

    suspend fun getById(id: Long): Attribute? = repository.getById(id)

    init {
        viewModelScope.launch {
            _selectedItemId.collectLatest { itemId ->
                if (itemId != null) {
                    repository.getAttributesByItem(itemId).collect { attributes ->
                        _attributesForItem.value = attributes
                    }
                } else {
                    _attributesForItem.value = emptyList()
                }
            }
        }
    }
}