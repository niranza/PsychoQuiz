package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.OutputOneDivisionSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface OutputOneDivisionSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutputOneDivisionSetting(outputOneDivisionSetting: OutputOneDivisionSetting)

    @Query("SELECT * FROM output_one_division_setting_table")
    suspend fun getAllOutputOneDivisionSettings(): List<OutputOneDivisionSetting>

    @Query("DELETE FROM output_one_division_setting_table")
    suspend fun deleteAllOutputOneDivisionSettings()

    @Query("SELECT * FROM output_one_division_setting_table")
    fun getAllOutputOneDivisionSettingsWithFlow(): Flow<List<OutputOneDivisionSetting>>
}