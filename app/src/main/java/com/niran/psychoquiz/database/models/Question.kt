package com.niran.psychoquiz.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "question_table")
data class Question(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "question_id")
    val questionId: Long = 0L,

    @Embedded
    val word: Word,

    @ColumnInfo(name = "answers")
    var answers: List<String> = listOf()

) {
    enum class Load { NEXT, PREVIOUS }
}

//@Entity(tableName = "question_table")
//data class Question(
//
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "question_id")
//    val questionId: Long = 0L,
//
//    @ColumnInfo(name = "word_id")
//    val wordId: Long = 0L,
//
//    @ColumnInfo(name = "question_word_text")
//    var questionWordText: String = "",
//
//    @ColumnInfo(name = "correct_answer")
//    val correctAnswer: String = "",
//
//    @ColumnInfo(name = "answers")
//    var answers: List<String> = listOf()
//
//) {
//    enum class Load { NEXT, PREVIOUS }
//}