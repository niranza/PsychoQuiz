package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import kotlin.reflect.KClass

class QuizSettingRepository(private val settingDao: SettingDao) {

    suspend fun insertBooleanSetting(booleanSetting: BooleanSetting) =
        when (booleanSetting) {
            is WordFirstLetterSetting ->
                settingDao.insertWordFirstLetterSetting(booleanSetting)
            is WordTypeSetting ->
                settingDao.insertWordTypeSetting(booleanSetting)
            else -> throw IllegalArgumentException("Unknown Boolean Setting Type")
        }

    suspend fun <T : BooleanSetting> selectAllSettings(clazz: KClass<T>, value: Boolean) =
        when (clazz) {
            WordFirstLetterSetting::class ->
                for (setting in settingDao.getAllWordFirstLetterSettings())
                    settingDao.insertWordFirstLetterSetting(setting.copy(settingValue = value))
            WordTypeSetting::class ->
                for (setting in settingDao.getAllWordTypeSettings())
                    settingDao.insertWordTypeSetting(setting.copy(settingValue = value))
            else -> throw IllegalArgumentException("Unknown Boolean Setting Type")
        }

    fun getAllWordFirstLetterSettingsWithFlow() = settingDao.getAllWordFirstLetterSettingsWithFlow()

    suspend fun getAllWordFirstLetterSettings() =
        settingDao.getAllWordFirstLetterSettings()

    fun getAllWordTypeSettingsWithFlow() = settingDao.getAllWordTypeSettingsWithFlow()

    suspend fun getAllWordTypeSettings() =
        settingDao.getAllWordTypeSettings()
}