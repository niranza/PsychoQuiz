package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "word_type_setting_table")
data class WordTypeSetting(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "setting_id")
    override val settingId: Int = 0,

    @ColumnInfo(name = "setting_value")
    override var settingValue: Boolean = true,

    @ColumnInfo(name = "setting_key")
    override var settingKey: Int = -1,

    @ColumnInfo(name = "setting_name")
    override val settingName: String = Word.Types.values()[settingKey].name.lowercase()
) : BooleanSetting() {

    object Constant : SettingConstant() {

        override val keyList =
            List(Word.Types.values().size) { i -> Word.Types.values()[i].ordinal }

        override val defaultSettingValues = List(Word.Types.values().size) { i ->
            i == Word.Types.NEUTRAL.ordinal || i == Word.Types.UNKNOWN.ordinal
        }
    }
}