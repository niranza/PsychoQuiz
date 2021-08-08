package com.niran.psychoquiz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "word_table")
data class Word(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    val wordId: Long = 0L,

    @ColumnInfo(name = "word_text")
    val wordText: String = "",

    @ColumnInfo(name = "word_translation")
    val wordTranslation: String = "",

    @ColumnInfo(name = "word_char")
    val wordFirstLetter: Char = wordText[0].lowercaseChar(),

    @ColumnInfo(name = "word_type")
    val wordType: Int = Types.NEUTRAL.ordinal

) {
    //region FirstLetter
    object FirstLetter : BooleanSetting.Interface {

        override val defaultSettingVal = true

        override val keyList = ('a'..'z').toList()

        override fun getAllValid(booleanSettings: List<BooleanSetting>): List<Char> {
            val result = mutableListOf<Char>()
            for (i in booleanSettings.indices)
                if (booleanSettings[i].settingValue)
                    result.add(keyList[i])
            return result
        }

        override fun getNames(): List<String> {
            val result = mutableListOf<String>()
            for (firstLetter in keyList)
                result.add(firstLetter.uppercase())
            return result
        }
    }
    //endregion FirstLetter

    //region WordType
    enum class Types(
        override val defaultSettingVal: Boolean = false
    ) : BooleanSetting.Interface {
        FAVORITE, UNKNOWN, NEUTRAL(true), KNOWN;

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
            for (wordType in values())
                result.add(wordType.name.lowercase())
            return result
        }
    }
    //endregion WordType
}