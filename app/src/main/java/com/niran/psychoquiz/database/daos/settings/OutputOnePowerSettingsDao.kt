package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.OutputOnePowerSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface OutputOnePowerSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutputOnePowerSetting(outputOnePowerSetting: OutputOnePowerSetting)

    @Query("SELECT * FROM output_one_power_setting_table")
    suspend fun getAllOutputOnePowerSettings(): List<OutputOnePowerSetting>

    @Query("DELETE FROM output_one_power_setting_table")
    suspend fun deleteAllOutputOnePowerSettings()

    @Query("SELECT * FROM output_one_power_setting_table")
    fun getAllOutputOnePowerSettingsWithFlow(): Flow<List<OutputOnePowerSetting>>
}