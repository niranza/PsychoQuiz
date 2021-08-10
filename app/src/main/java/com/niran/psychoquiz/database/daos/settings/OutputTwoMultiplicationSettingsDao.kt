package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.OutputTwoMultiplicationSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface OutputTwoMultiplicationSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutputTwoMultiplicationSetting(outputTwoMultiplicationSetting: OutputTwoMultiplicationSetting)

    @Query("SELECT * FROM output_two_multiplication_setting_table")
    suspend fun getAllOutputTwoMultiplicationSettings(): List<OutputTwoMultiplicationSetting>

    @Query("DELETE FROM output_two_multiplication_setting_table")
    suspend fun deleteAllOutputTwoMultiplicationSettings()

    @Query("SELECT * FROM output_two_multiplication_setting_table")
    fun getAllOutputTwoMultiplicationSettingsWithFlow(): Flow<List<OutputTwoMultiplicationSetting>>
}