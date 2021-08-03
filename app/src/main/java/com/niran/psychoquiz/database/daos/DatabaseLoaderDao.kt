package com.niran.psychoquiz.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.DatabaseLoader

@Dao
interface DatabaseLoaderDao {

    /**
     * this will force the database to create itself
     * then, the RoomCallBack class will call...
     * ...onCreate...
     * ...onOpen...
     */
    @Query("DELETE FROM database_loader_table WHERE database_loader_id = 0")
    suspend fun loadData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatabaseLoader(databaseLoader: DatabaseLoader)

    @Query("SELECT * FROM database_loader_table WHERE database_loader_id = 1")
    suspend fun getDatabaseLoader(): DatabaseLoader

}