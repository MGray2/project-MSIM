package com.ms.im

import android.app.Application
import com.ms.im.database.AppDatabase
import com.ms.im.database.factories.*
import com.ms.im.database.manager.AttributeManager
import com.ms.im.database.repositories.*


class MyApp : Application() {

    // Singleton database instance
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositories
    private val groupRepository by lazy { GroupRepository(database.groupDao()) }
    private val itemRepository by lazy { ItemRepository(database.itemDao()) }
    private val attributeTemplateRepository by lazy {
        AttributeTemplateRepository(database.attributeTemplateDao())}
    private val attributeInstanceRepository by lazy {
        AttributeInstanceRepository(database.attributeInstanceDao())}

    // Managers
    private val attributeManager by lazy {
        AttributeManager(
            attributeTemplateRepository,
            attributeInstanceRepository,
            itemRepository,
            database
        )
    }

    // ViewModel Factories
    val groupViewModelFactory by lazy { GroupFactory(groupRepository) }
    val itemViewModelFactory by lazy { ItemFactory(itemRepository) }
    val attributeTemplateFactory by lazy { AttributeTemplateFactory(attributeTemplateRepository, attributeManager)}
    val attributeInstanceFactory by lazy { AttributeInstanceFactory(attributeInstanceRepository, attributeManager)}
}