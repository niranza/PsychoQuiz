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
    enum class FirstLetter(
        override var settingValue: Boolean = true
    ) : BooleanSetting.Interface {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

        override fun getKey(): Char = name.lowercase().toCharArray()[0]

        override fun getValue(): Boolean = settingValue

        override fun getAllValid(): List<Char> {
            val result = mutableListOf<Char>()
            for (firstLetter in values())
                if (firstLetter.settingValue)
                    result.add(firstLetter.getKey())
            return result
        }

        override fun getNames(): List<String> {
            val result = mutableListOf<String>()
            for (firstLetter in values())
                result.add(firstLetter.name)
            return result
        }
    }
    //endregion FirstLetter

    //region WordType
    enum class Types(
        override var settingValue: Boolean = false
    ) : BooleanSetting.Interface {
        FAVORITE, UNKNOWN, NEUTRAL(true), KNOWN;

        override fun getKey(): Int = ordinal

        override fun getValue(): Boolean = settingValue

        override fun getAllValid(): List<Int> {
            val result = mutableListOf<Int>()
            for (wordType in values())
                if (wordType.settingValue)
                    result.add(wordType.getKey())
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