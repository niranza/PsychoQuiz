package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "word_first_letter_setting_table")
data class WordFirstLetterSetting(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "setting_id")
    override val settingId: Int = 0,

    @ColumnInfo(name = "setting_value")
    override var settingValue: Boolean = true,

    @ColumnInfo(name = "setting_key")
    override var settingKey: Char = '0',

    @ColumnInfo(name = "setting_name")
    override val settingName: String = settingKey.toString().uppercase()
) : BooleanSetting() {

    object Constant : SettingConstant() {

        override val keyList = ('a'..'z').toList()
    }
}
