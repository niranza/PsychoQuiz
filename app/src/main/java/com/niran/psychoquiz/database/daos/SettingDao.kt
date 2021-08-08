package com.niran.psychoquiz.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {

    //region WordFirstLetterSetting
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordFirstLetterSetting(wordFirstLetterSetting: WordFirstLetterSetting)

    @Query("SELECT * FROM word_first_letter_setting_table")
    suspend fun getAllWordFirstLetterSettings(): List<WordFirstLetterSetting>

    @Query("DELETE FROM word_first_letter_setting_table")
    suspend fun deleteAllWordFirstLetterSettings()

    @Query("SELECT * FROM word_first_letter_setting_table")
    fun getAllWordFirstLetterSettingsWithFlow(): Flow<List<WordFirstLetterSetting>>
    //endregion WordFirstLetterSetting

    //region WordTypeSetting
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordTypeSetting(wordTypeSetting: WordTypeSetting)

    @Query("SELECT * FROM word_type_setting_table")
    suspend fun getAllWordTypeSettings(): List<WordTypeSetting>

    @Query("DELETE FROM word_type_setting_table")
    suspend fun deleteAllWordTypeSettings()

    @Query("SELECT * FROM word_type_setting_table")
    fun getAllWordTypeSettingsWithFlow(): Flow<List<WordTypeSetting>>
    //endregion WordTypeSetting
}