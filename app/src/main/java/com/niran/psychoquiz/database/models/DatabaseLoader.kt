package com.niran.psychoquiz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "database_loader_table")
data class DatabaseLoader(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "database_loader_id")
    val databaseLoaderId: Int = 1,

    @ColumnInfo(name = "is_loaded")
    val isLoaded: Boolean = false

)
