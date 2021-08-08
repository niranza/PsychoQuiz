package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "word_type_setting_table")
data class WordTypeSetting(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "setting_id")
    override val settingId: Int = -1,

    @ColumnInfo(name = "setting_value")
    override var settingValue: Boolean = true,
) : BooleanSetting(settingId, settingValue) {

    object Constant: Interface {
        override val defaultSettingVal: Boolean = false

        val defaultSettingValList = List<Boolean>(4){ i -> i != 3 }

        override val keyList = List(4) { i -> i }

        override fun getAllValid(booleanSettings: List<BooleanSetting>): List<Int> {
            val result = mutableListOf<Int>()
            for (i in booleanSettings.indices)
                if (booleanSettings[i].settingValue)
                    result.add(keyList[i])
            return result
        }

        override fun getNames(): List<String> {
            val result = mutableListOf<String>()
            for (wordType in Word.Types.values())
                result.add(wordType.name.lowercase())
            return result
        }
    }

}