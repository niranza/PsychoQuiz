package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.OutputOneMultiplicationSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface OutputOneMultiplicationSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutputOneMultiplicationSetting(outputOneMultiplicationSetting: OutputOneMultiplicationSetting)

    @Query("SELECT * FROM output_one_multiplication_setting_table")
    suspend fun getAllOutputOneMultiplicationSettings(): List<OutputOneMultiplicationSetting>

    @Query("DELETE FROM output_one_multiplication_setting_table")
    suspend fun deleteAllOutputOneMultiplicationSettings()

    @Query("SELECT * FROM output_one_multiplication_setting_table")
    fun getAllOutputOneMultiplicationSettingsWithFlow(): Flow<List<OutputOneMultiplicationSetting>>
}