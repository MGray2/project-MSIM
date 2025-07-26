package com.ms.im.database.manager


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.ms.im.AttributeType
import com.ms.im.OrderDirection
import com.ms.im.database.AppDatabase
import com.ms.im.database.entities.AttributeInstance
import com.ms.im.database.entities.AttributeTemplate
import com.ms.im.database.entities.Item
import com.ms.im.database.repositories.AttributeInstanceRepository
import com.ms.im.database.repositories.AttributeTemplateRepository
import com.ms.im.database.repositories.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AttributeManager(
    private val templateRepository: AttributeTemplateRepository,
    private val instanceRepository: AttributeInstanceRepository,
    private val itemRepository: ItemRepository,
    private val database: AppDatabase
) {

    suspend fun updateTemplatesAndBackFillInstances(itemId: Long, newTemplates: List<AttributeTemplate>) {
        database.withTransaction {
            // normalize and assign positions
            val normalizedNewTemplates = newTemplates
                .sortedBy { it.position }
                .mapIndexed { index, attr -> attr.copy(position = index, itemId = itemId) }

            // get existing templates before deletion
            val oldTemplates = templateRepository.getAllByItem(itemId).first()

            // get all existing instance values by (itemId + template.stableId)
            val instancesByItem = mutableMapOf<Long, Map<String, AttributeInstance>>()
            val items = itemRepository.getInstancesByTemplate(itemId).first()

            for (item in items) {
                val instances = instanceRepository.getAllByItem(item.id).first()
                val instanceMap = mutableMapOf<String, AttributeInstance>()

                for (instance in instances) {
                    val oldTemplate = oldTemplates.find { it.id == instance.templateId }
                    if (oldTemplate != null) {
                        instanceMap[oldTemplate.stableId] = instance
                    }
                }

                instancesByItem[item.id] = instanceMap
            }

            // replace templates
            templateRepository.replaceAttributes(itemId, normalizedNewTemplates)

            // re-obtain templates with new IDs
            val updatedTemplates = templateRepository.getAllByItem(itemId).first()
            val templateMap = updatedTemplates.associateBy { it.stableId }

            // for each item instance, insert new AttributeInstances
            for (item in items) {
                val oldInstanceMap = instancesByItem[item.id] ?: emptyMap()

                val newInstances = templateMap.mapNotNull { (stableId, newTemplate) ->
                    val oldInstance = oldInstanceMap[stableId]
                    AttributeInstance(
                        itemId = item.id,
                        templateId = newTemplate.id,
                        valueText = oldInstance?.valueText,
                        valueNumber = oldInstance?.valueNumber,
                        valueDecimal = oldInstance?.valueDecimal,
                        valueBool = oldInstance?.valueBool
                    )
                }

                instanceRepository.deleteAllByItem(item.id)
                instanceRepository.insertAll(newInstances)
            }
        }
    }

    fun getPagedItemsSortedByAttribute(
        templateId: Long,
        sortAttrTemplateId: Long,
        type: AttributeType,
        order: OrderDirection
    ): Flow<PagingData<Item>> {
        val dao = database.itemWithAttributesDao() // a DAO dedicated to join-based queries

        val pagingSourceFactory = when (type to order) {
            AttributeType.TEXT to OrderDirection.Asc -> {
                { dao.getItemsSortedByTextAsc(templateId, sortAttrTemplateId) }
            }
            AttributeType.TEXT to OrderDirection.Desc -> {
                { dao.getItemsSortedByTextDesc(templateId, sortAttrTemplateId) }
            }
            AttributeType.TAG to OrderDirection.Asc -> {
                { dao.getItemsSortedByTextAsc(templateId, sortAttrTemplateId)}
            }
            AttributeType.TAG to OrderDirection.Desc -> {
                { dao.getItemsSortedByTextDesc(templateId, sortAttrTemplateId) }
            }
            AttributeType.NUMBER to OrderDirection.Asc -> {
                { dao.getItemsSortedByNumberAsc(templateId, sortAttrTemplateId) }
            }
            AttributeType.NUMBER to OrderDirection.Desc -> {
                { dao.getItemsSortedByNumberDesc(templateId, sortAttrTemplateId) }
            }
            AttributeType.DECIMAL to OrderDirection.Asc -> {
                { dao.getItemsSortedByDecimalAsc(templateId, sortAttrTemplateId)}
            }
            AttributeType.DECIMAL to OrderDirection.Desc -> {
                { dao.getItemsSortedByDecimalDesc(templateId, sortAttrTemplateId)}
            }
            AttributeType.STATE to OrderDirection.Asc -> {
                { dao.getItemsSortedByBoolAsc(templateId, sortAttrTemplateId) }
            }
            AttributeType.STATE to OrderDirection.Desc -> {
                { dao.getItemsSortedByBoolDesc(templateId, sortAttrTemplateId)}
            }
            else -> {
                { dao.getItemsSortedByTextAsc(templateId, sortAttrTemplateId) }
            }
        }

        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}