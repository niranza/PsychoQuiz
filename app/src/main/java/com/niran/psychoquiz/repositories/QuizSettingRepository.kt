package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting

class QuizSettingRepository(private val settingDao: SettingDao) {

    //region WordFirstLetterSetting
    suspend fun insertWordFirstLetterSetting(id: Int, value: Boolean) =
        settingDao.insertWordFirstLetterSetting(WordFirstLetterSetting(id, value))

    fun getAllWordFirstLetterSettings() = settingDao.getAllWordFirstLetterSettings()
    //endregion WordFirstLetterSetting

    //region WordTypeSetting
    suspend fun insertWordTypeSetting(id: Int, value: Boolean) =
        settingDao.insertWordTypeSetting(WordTypeSetting(id, value))

    fun getAllWordTypeSettings() = settingDao.getAllWordTypeSettings()
    //endregion WordTypeSetting
}