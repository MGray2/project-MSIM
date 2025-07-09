package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ms.im.SortOrder
import com.ms.im.database.entities.Group
import com.ms.im.database.repositories.GroupRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    // ** Viewmodel variables

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

    // Search bar variable
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // For sort cycle button
    private val _sortOrder = MutableStateFlow(SortOrder.NameAsc)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _randomSeed = MutableStateFlow(0)
    val randomSeed: StateFlow<Int> = _randomSeed

    // Get
    suspend fun getById(id: Long): Group? = repository.getById(id)

    // Setters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        if (order == SortOrder.Random) {
            _randomSeed.value = kotlin.random.Random.nextInt()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagedGroups = combine(searchQuery, sortOrder, randomSeed) { query, order, seed ->
        Triple(query, order, seed)
    }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, order, seed) ->
            repository.getPagedGroupsFilteredSorted(query, order, seed) }
        .cachedIn(viewModelScope)

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