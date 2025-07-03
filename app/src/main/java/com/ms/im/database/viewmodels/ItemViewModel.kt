package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.ms.im.database.entities.Item
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    private val _itemsInGroup = MutableStateFlow<List<Item>>(emptyList())
    val itemsInGroup: StateFlow<List<Item>> = _itemsInGroup.asStateFlow()

    fun selectGroup(groupId: Long) {
        _selectedGroupId.value = groupId
    }

    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: Item) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: Item) = viewModelScope.launch {
        repository.delete(item)
    }

    suspend fun getById(id: Long): Item? = repository.getById(id)

    init {
        viewModelScope.launch {
            _selectedGroupId.collectLatest { groupId ->
                if (groupId != null) {
                    repository.getItemsByGroup(groupId).collect { items ->
                        _itemsInGroup.value = items
                    }
                } else {
                    _itemsInGroup.value = emptyList()
                }
            }
        }
    }
}