package com.ms.im.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ms.im.database.converters.AttributeTypeConverter
import com.ms.im.database.daos.*
import com.ms.im.database.entities.*

@Database(
    entities = [Group::class, Item::class, Attribute::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AttributeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun itemDao(): ItemDao
    abstract fun attributeDao(): AttributeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}