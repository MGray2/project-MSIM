package com.ms.im.database.converters

import androidx.room.TypeConverter
import com.ms.im.AttributeType

class AttributeTypeConverter {
    @TypeConverter
    fun fromAttributeType(type: AttributeType): String = type.name

    @TypeConverter
    fun toAttributeType(value: String): AttributeType = AttributeType.valueOf(value)
}