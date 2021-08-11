package com.niran.psychoquiz.utils

import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word

object QuizAiUtil {

    fun getNewWord(wrongAnswerCount: Int, question: Question): Result {
        val currentWordType = Word.Types.values()[question.word.wordType]
        val newWordType = when (currentWordType) {
            Word.Types.FAVORITE -> currentWordType
            Word.Types.UNKNOWN -> when (wrongAnswerCount) {
                0 -> Word.Types.KNOWN
                else -> currentWordType
            }
            Word.Types.NEUTRAL -> when (wrongAnswerCount) {
                0 -> Word.Types.KNOWN
                in 2..3 -> Word.Types.UNKNOWN
                else -> currentWordType
            }
            Word.Types.KNOWN -> when (wrongAnswerCount) {
                in 2..3 -> Word.Types.UNKNOWN
                else -> currentWordType
            }
        }
        val showDialog: Boolean = currentWordType == Word.Types.UNKNOWN && wrongAnswerCount == 0
        return Result(
            with(question) { copy(word = word.copy(wordType = newWordType.ordinal)) },
            showDialog
        )
    }

    data class Result(val newQuestion: Question, val showDialog: Boolean)
}