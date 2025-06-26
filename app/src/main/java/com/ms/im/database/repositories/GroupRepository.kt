package com.ms.im.database.repositories

import com.ms.im.database.daos.GroupDao
import com.ms.im.database.entities.Group
import kotlinx.coroutines.flow.Flow

class GroupRepository(private val dao: GroupDao) {

    // Get all instances
    val allGroups: Flow<List<Group>> = dao.getAllGroups()

    // Get one instance by id
    suspend fun getById(id: Long): Group? {
        return dao.getGroupById(id)
    }

    // Insert
    suspend fun insert(group: Group): Long {
        return dao.insert(group)
    }

    // Update
    suspend fun update(group: Group) {
        dao.update(group)
    }

    // Delete
    suspend fun delete(group: Group) {
        dao.delete(group)
    }
}