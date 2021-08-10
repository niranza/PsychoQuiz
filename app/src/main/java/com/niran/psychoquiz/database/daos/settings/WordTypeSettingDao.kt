package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.WordTypeSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface WordTypeSettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordTypeSetting(wordTypeSetting: WordTypeSetting)

    @Query("SELECT * FROM word_type_setting_table")
    suspend fun getAllWordTypeSettings(): List<WordTypeSetting>

    @Query("DELETE FROM word_type_setting_table")
    suspend fun deleteAllWordTypeSettings()

    @Query("SELECT * FROM word_type_setting_table")
    fun getAllWordTypeSettingsWithFlow(): Flow<List<WordTypeSetting>>
}