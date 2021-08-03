package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting

class SettingRepository(private val settingDao: SettingDao) {

    //region WordFirstLetterSetting
    suspend fun insertWordFirstLetterSetting(wordFirstLetterSetting: WordFirstLetterSetting) =
        settingDao.insertWordFirstLetterSetting(wordFirstLetterSetting)

    suspend fun getAllWordFirstLetterSettings() = settingDao.getAllWordFirstLetterSettings()
    //endregion WordFirstLetterSetting

    //region WordTypeSetting
    suspend fun insertWordTypeSetting(wordTypeSetting: WordTypeSetting) =
        settingDao.insertWordTypeSetting(wordTypeSetting)

    suspend fun getAllWordTypeSettings() = settingDao.getAllWordTypeSettings()
    //endregion WordTypeSetting
}