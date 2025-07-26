package com.ms.im.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.ms.im.database.entities.Item

@Dao
interface ItemWithAttributesDao {
    // TEXT or TAG attribute - Ascending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueText COLLATE NOCASE ASC, i.id ASC
    """)
    fun getItemsSortedByTextAsc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // TEXT or TAG attribute - Descending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueText COLLATE NOCASE DESC, i.id ASC
    """)
    fun getItemsSortedByTextDesc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // NUMBER attribute - Ascending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueNumber ASC, i.id ASC
    """)
    fun getItemsSortedByNumberAsc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // NUMBER attribute - Descending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueNumber DESC, i.id ASC
    """)
    fun getItemsSortedByNumberDesc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // DECIMAL attribute - Ascending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueDecimal ASC, i.id ASC
    """)
    fun getItemsSortedByDecimalAsc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // DECIMAL attribute - Descending
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueDecimal DESC, i.id ASC
    """)
    fun getItemsSortedByDecimalDesc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // STATE attribute - Ascending (false → true)
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueBool ASC, i.id ASC
    """)
    fun getItemsSortedByBoolAsc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>

    // STATE attribute - Descending (true → false)
    @Query("""
        SELECT i.* FROM item_table i
        LEFT JOIN attribute_instance_table ai
            ON ai.itemId = i.id AND ai.templateId = :attributeTemplateId
        WHERE i.isTemplate = 0 AND i.templateId = :templateId
        ORDER BY ai.valueBool DESC, i.id ASC
    """)
    fun getItemsSortedByBoolDesc(
        templateId: Long,
        attributeTemplateId: Long
    ): PagingSource<Int, Item>
}
