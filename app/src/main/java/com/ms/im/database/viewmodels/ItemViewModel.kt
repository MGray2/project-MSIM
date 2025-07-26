package com.ms.im.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ms.im.OrderDirection
import com.ms.im.SortField
import com.ms.im.SortOrder
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.ms.im.database.entities.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    // ** Viewmodel variables **

    // Search bar variable
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // For sort cycle button
    private val _sortOrder = MutableStateFlow<SortOrder>(SortOrder.Field(SortField.Name, OrderDirection.Asc))
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _randomSeed = MutableStateFlow(0)
    private val randomSeed: StateFlow<Int> = _randomSeed

    private val _itemsInGroup = MutableStateFlow<List<Item>>(emptyList())
    val itemsInGroup: StateFlow<List<Item>> = _itemsInGroup.asStateFlow()

    // Currently selected Group (nullable)
    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId

    // Currently selected Item (nullable)
    private val _selectedTemplateId = MutableStateFlow<Long?>(null)
    val selectedTemplateId: StateFlow<Long?> = _selectedTemplateId

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pagedTemplates: Flow<PagingData<Item>> =
        combine(searchQuery, sortOrder, selectedGroupId, randomSeed) { query, order, groupId, seed ->
            TemplateQueryParams(query, order, groupId, seed)
    }
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { params ->
                val (query, order, groupId, seed) = params
                if (groupId != null) {
                    repository.getPagedTemplatesFilteredSorted(query, groupId, order, seed)
                } else {
                    emptyFlow()
                }
            }
            .cachedIn(viewModelScope)

    // ** Setters **
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setSortField(field: SortField) {
        val current = _sortOrder.value
        if (current is SortOrder.Field) {
            _sortOrder.value = SortOrder.Field(field, current.direction)
        }
    }

    fun setSortDirection(direction: OrderDirection) {
        val current = _sortOrder.value
        if (current is SortOrder.Field) {
            _sortOrder.value = SortOrder.Field(current.field, direction)
        }
    }

    fun setSortRandom() {
        _sortOrder.value = SortOrder.Random
        _randomSeed.value = Random.nextInt()
    }

    fun setSelectedGroupId(id: Long?) {
        _selectedGroupId.value = id
    }

    fun resetSelectedGroupId() {
        _selectedGroupId.value = null
    }

    fun setSelectedTemplateId(id: Long?) {
        _selectedTemplateId.value = id
    }

    fun resetSelectedTemplateId() {
        _selectedTemplateId.value = null
    }

    fun selectGroup(groupId: Long) {
        _selectedGroupId.value = groupId
    }

    // ** Get **
    suspend fun getById(id: Long): Item? = repository.getById(id)

    // Get all instances from a given item template
    fun getInstancesByTemplate(templateId: Long): Flow<List<Item>> {
        return repository.getInstancesByTemplate(templateId)
    }

    // ** Insert **
    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }

    suspend fun insertReturn(item: Item): Long {
        return repository.insert(item)
    }

    // ** Update **
    fun update(item: Item) = viewModelScope.launch {
        repository.update(item)
    }

    // ** Delete **
    fun delete(item: Item) = viewModelScope.launch {
        repository.delete(item)
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            val target = repository.getById(id)
            if (target != null) repository.delete(target)
        }
    }

    private data class TemplateQueryParams(
        val query: String,
        val sortOrder: SortOrder,
        val groupId: Long?,
        val seed: Int
    )

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