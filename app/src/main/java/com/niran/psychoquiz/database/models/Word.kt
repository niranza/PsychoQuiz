package com.niran.psychoquiz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    enum class FirstLetter(
        var settingValue: Boolean = true,
    ) {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

        fun getChar(): Char = name.lowercase().toCharArray()[0]
    }

    enum class Types(
        var settingValue: Boolean = false
    ) {
        FAVORITE, UNKNOWN, NEUTRAL(true), KNOWN;

        fun getType(): Int = ordinal
    }
}