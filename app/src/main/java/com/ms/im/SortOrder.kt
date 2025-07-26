package com.ms.im

enum class SortField {
    Name
}

enum class OrderDirection {
    Asc,
    Desc
}

sealed class SortOrder {
    data class Field(val field: SortField, val direction: OrderDirection) : SortOrder()
    data object Random : SortOrder()
}