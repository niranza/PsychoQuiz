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

    @ColumnInfo(name = "word_type")
    val wordType: Int = Types.NEUTRAL.ordinal,

    @ColumnInfo(name = "times_answered_correct")
    val timesAnsweredCorrect: Int = 0
) {
    enum class Types { FAVORITE, UNKNOWN, NEUTRAL, KNOWN; }
}