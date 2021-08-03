package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.DatabaseLoaderDao

class DatabaseLoaderRepository(private val databaseLoaderDao: DatabaseLoaderDao) {

    suspend fun loadDatabase() = databaseLoaderDao.loadData()

}