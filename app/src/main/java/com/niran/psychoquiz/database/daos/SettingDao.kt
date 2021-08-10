package com.niran.psychoquiz.database.daos

import androidx.room.Dao
import com.niran.psychoquiz.database.daos.settings.*

@Dao
interface SettingDao :
    WordFirstLetterSettingDao,
    WordTypeSettingDao,
    OutputOneMultiplicationSettingsDao,
    OutputOneDivisionSettingsDao,
    OutputOnePowerSettingsDao,
    OutputTwoMultiplicationSettingsDao,
    OutputTwoDivisionSettingsDao