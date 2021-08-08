package com.niran.psychoquiz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_table")
data class Question(

    /**
     * this is also the id of the
     * word that this question presents
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "question_id")
    val questionId: Long = 0L,

    val wordId: Long = 0L,

    @ColumnInfo(name = "question_word_text")
    var questionWordText: String = "",

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String = "",

    @ColumnInfo(name = "answers")
    var answers: List<String> = listOf()

) {
    enum class Load { NEXT, PREVIOUS }
}
