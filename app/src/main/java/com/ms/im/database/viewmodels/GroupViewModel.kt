package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ms.im.database.entities.Group
import com.ms.im.database.repositories.GroupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    // All instances
    val allGroups: StateFlow<List<Group>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    // Delete
    fun delete(group: Group) = viewModelScope.launch {
        repository.delete(group)
    }
}