package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "output_two_multiplication_setting_table")
data class OutputTwoMultiplicationSetting(

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

    object Constant : SettingConstant {

        override val keyList = (OUTPUT_TWO_MIN_NUMBER..OUTPUT_TWO_MAX_NUMBER).toList()
    }

    companion object {
        const val OUTPUT_TWO_MIN_NUMBER = 1
        const val OUTPUT_TWO_MAX_NUMBER = 12
    }
}
