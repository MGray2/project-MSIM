package com.ms.im

import android.app.Application
import com.ms.im.database.AppDatabase
import com.ms.im.database.factories.*
import com.ms.im.database.repositories.*


class MyApp : Application() {

    // Singleton database instance
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositories
    private val groupRepository by lazy { GroupRepository(database.groupDao()) }
    private val itemRepository by lazy { ItemRepository(database.itemDao()) }
    private val attributeRepository by lazy { AttributeRepository(database.attributeDao()) }

    // ViewModel Factories
    val groupViewModelFactory by lazy { GroupFactory(groupRepository) }
    val itemViewModelFactory by lazy { ItemFactory(itemRepository) }
    val attributeViewModelFactory by lazy { AttributeFactory(attributeRepository) }
}