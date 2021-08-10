package com.niran.psychoquiz.database.daos.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface WordFirstLetterSettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordFirstLetterSetting(wordFirstLetterSetting: WordFirstLetterSetting)

    @Query("SELECT * FROM word_first_letter_setting_table")
    suspend fun getAllWordFirstLetterSettings(): List<WordFirstLetterSetting>

    @Query("DELETE FROM word_first_letter_setting_table")
    suspend fun deleteAllWordFirstLetterSettings()

    @Query("SELECT * FROM word_first_letter_setting_table")
    fun getAllWordFirstLetterSettingsWithFlow(): Flow<List<WordFirstLetterSetting>>
}