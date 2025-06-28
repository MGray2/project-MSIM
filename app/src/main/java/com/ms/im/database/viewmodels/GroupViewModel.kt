package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.Group
import com.ms.im.database.repositories.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    // All instances
    val allGroups: StateFlow<List<Group>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Viewmodel storage for one selected instance
    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId

    fun selectGroup(id: Long) {
        _selectedGroupId.value = id
    }

    fun resetSelectedGroup() {
        _selectedGroupId.value = null
    }

    // Get one instance by id
    suspend fun getById(id: Long): Group? = repository.getById(id)

    // Insert
    fun insert(group: Group) = viewModelScope.launch {
        repository.insert(group)
    }

    // Update
    fun update(group: Group) = viewModelScope.launch {
        repository.update(group)
    }

    fun updateById(groupId: Long, newGroupName: String) {
        viewModelScope.launch {
            val target = repository.getById(groupId)
            if (target != null) {
                val updatedGroup = target.copy(name = newGroupName)
                repository.update(updatedGroup)
            }
        }
    }

    // Delete
    fun delete(group: Group) = viewModelScope.launch {
        repository.delete(group)
    }

    fun deleteById(groupId: Long) {
        viewModelScope.launch {
            val target = repository.getById(groupId)
            if (target != null) repository.delete(target)
        }
    }
}