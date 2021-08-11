package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "output_one_power_setting_table")
data class OutputOnePowerSetting(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "setting_id")
    override val settingId: Int = 0,

    @ColumnInfo(name = "setting_value")
    override var settingValue: Boolean = true,

    @ColumnInfo(name = "setting_key")
    override var settingKey: Int = -1,

    @ColumnInfo(name = "setting_name")
    override val settingName: String = settingKey.toString()
) : BooleanSetting() {

    object Constant : SettingConstant() {

        override val keyList = (OUTPUT_ONE_MIN_NUMBER..OUTPUT_ONE_MAX_NUMBER).toList() +
                listOf(25)

        fun getPower(outputOne: Int): List<Int> = when (outputOne) {
            2 -> (2..8).toList()
            3 -> (2..5).toList()
            in 4..7 -> (2..3).toList()
            in ((8..20) + listOf(25)) -> listOf(2)
            else -> throw IllegalArgumentException("Unknown Key In PowerSettings Class")
        }
    }

    companion object {
        const val OUTPUT_ONE_MIN_NUMBER = 2
        const val OUTPUT_ONE_MAX_NUMBER = 20
    }
}
