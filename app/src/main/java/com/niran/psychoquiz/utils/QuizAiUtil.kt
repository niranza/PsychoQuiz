package com.niran.psychoquiz.utils

import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word

object QuizAiUtil {

    fun getNewWord(wrongAnswerCount: Int, question: Question): Result {

        val currentWordType = Word.Types.values()[question.word.wordType]
        var answeredCorrect = false
        val newWordType = when (currentWordType) {
            Word.Types.FAVORITE -> currentWordType
            Word.Types.UNKNOWN -> when (wrongAnswerCount) {
                0 -> {
                    answeredCorrect = true
                    Word.Types.KNOWN
                }
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

        val questionWithNewAnswerCount = with(question) {
            copy(
                word = word.copy(
                    timesAnsweredCorrect =
                    if (answeredCorrect) word.timesAnsweredCorrect + 1
                    else word.timesAnsweredCorrect
                )
            )
        }

        val questionWithNewWordType = with(questionWithNewAnswerCount) {
            copy(word = word.copy(wordType = newWordType.ordinal))
        }

        val showDialog = wrongAnswerCount == 0
                && question.word.timesAnsweredCorrect % TIMES_UNTIL_NEXT_DIALOG_POP == 0

        val isQuestionUnknown = currentWordType == Word.Types.UNKNOWN

        return Result(
            questionWithNewAnswerCount,
            questionWithNewWordType,
            isQuestionUnknown,
            showDialog
        )
    }

    data class Result(
        val questionWithNewAnswerCount: Question,
        val questionWithNewWordType: Question,
        val questionUnknown: Boolean,
        val showDialog: Boolean
    )

    private const val TIMES_UNTIL_NEXT_DIALOG_POP = 3
}