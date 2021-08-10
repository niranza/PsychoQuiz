package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.models.settings.*
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.utils.enums.MathType
import kotlinx.coroutines.flow.flowOf

class MathQuizSettingRepository(private val settingDao: SettingDao) {

    suspend fun insertBooleanSetting(booleanSetting: BooleanSetting) =
        when (booleanSetting) {
            is OutputOneMultiplicationSetting ->
                settingDao.insertOutputOneMultiplicationSetting(booleanSetting)
            is OutputTwoMultiplicationSetting ->
                settingDao.insertOutputTwoMultiplicationSetting(booleanSetting)
            is OutputOneDivisionSetting ->
                settingDao.insertOutputOneDivisionSetting(booleanSetting)
            is OutputTwoDivisionSetting ->
                settingDao.insertOutputTwoDivisionSetting(booleanSetting)
            is OutputOnePowerSetting ->
                settingDao.insertOutputOnePowerSetting(booleanSetting)
            else -> throw IllegalArgumentException("Unknown Boolean Setting Type")
        }

    suspend fun selectAllOutputOneSettings(mathType: MathType, value: Boolean) = when (mathType) {
        MathType.MULTIPLICATION ->
            for (setting in settingDao.getAllOutputOneMultiplicationSettings())
                settingDao.insertOutputOneMultiplicationSetting(setting.copy(settingValue = value))
        MathType.DIVISION ->
            for (setting in settingDao.getAllOutputOneDivisionSettings())
                settingDao.insertOutputOneDivisionSetting(setting.copy(settingValue = value))
        MathType.POWER ->
            for (setting in settingDao.getAllOutputOnePowerSettings())
                settingDao.insertOutputOnePowerSetting(setting.copy(settingValue = value))
    }

    suspend fun selectAllOutputTwoSettings(mathType: MathType, value: Boolean) = when (mathType) {
        MathType.MULTIPLICATION ->
            for (setting in settingDao.getAllOutputTwoMultiplicationSettings())
                settingDao.insertOutputTwoMultiplicationSetting(setting.copy(settingValue = value))
        MathType.DIVISION ->
            for (setting in settingDao.getAllOutputTwoDivisionSettings())
                settingDao.insertOutputTwoDivisionSetting(setting.copy(settingValue = value))
        MathType.POWER -> Unit
    }

    fun getAllOutputOneSettingsWithFlow(mathType: MathType) =
        when (mathType) {
            MathType.MULTIPLICATION ->
                settingDao.getAllOutputOneMultiplicationSettingsWithFlow()
            MathType.DIVISION ->
                settingDao.getAllOutputOneDivisionSettingsWithFlow()
            MathType.POWER ->
                settingDao.getAllOutputOnePowerSettingsWithFlow()
        }

    fun getAllOutputTwoSettingsWithFlow(mathType: MathType) =
        when (mathType) {
            MathType.MULTIPLICATION ->
                settingDao.getAllOutputTwoMultiplicationSettingsWithFlow()
            MathType.DIVISION ->
                settingDao.getAllOutputTwoDivisionSettingsWithFlow()
            MathType.POWER -> flowOf()
        }

    suspend fun getAllOutputOneSettings(mathType: MathType) =
        when (mathType) {
            MathType.MULTIPLICATION ->
                settingDao.getAllOutputOneMultiplicationSettings()
            MathType.DIVISION ->
                settingDao.getAllOutputOneDivisionSettings()
            MathType.POWER ->
                settingDao.getAllOutputOnePowerSettings()
        }

    suspend fun getAllOutputTwoSettings(mathType: MathType) =
        when (mathType) {
            MathType.MULTIPLICATION ->
                settingDao.getAllOutputTwoMultiplicationSettings()
            MathType.DIVISION ->
                settingDao.getAllOutputTwoDivisionSettings()
            MathType.POWER -> listOf()
        }
}