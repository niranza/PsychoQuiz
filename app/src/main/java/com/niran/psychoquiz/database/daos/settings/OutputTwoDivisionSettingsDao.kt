package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.OutputTwoDivisionSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface OutputTwoDivisionSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutputTwoDivisionSetting(outputTwoDivisionSetting: OutputTwoDivisionSetting)

    @Query("SELECT * FROM output_two_division_setting_table")
    suspend fun getAllOutputTwoDivisionSettings(): List<OutputTwoDivisionSetting>

    @Query("DELETE FROM output_two_division_setting_table")
    suspend fun deleteAllOutputTwoDivisionSettings()

    @Query("SELECT * FROM output_two_division_setting_table")
    fun getAllOutputTwoDivisionSettingsWithFlow(): Flow<List<OutputTwoDivisionSetting>>
}