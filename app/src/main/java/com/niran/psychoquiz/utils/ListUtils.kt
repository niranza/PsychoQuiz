package com.niran.psychoquiz.utils

import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word


//region QuestionListUtils

fun List<Question>.hasInvalidQuestions(vararg words: Word): Boolean{
    for (question in this)
        if (!words.contains(question.word))
            return true
    return false
}

//endregion QuestionListUtils


//region WordListUtils
fun List<Word>.sortByType() = sortedBy { it.wordType }

fun List<Word>.filterWordListBySearchQuery(searchQuery: String?) =
    filter {
        it.wordText.replace("\\s".toRegex(), "")
            .contains(searchQuery ?: "", true)
                || it.wordTranslation.replace("\\s".toRegex(), "")
            .contains(searchQuery ?: "", true)
    }

fun List<Word>.filterByWordTypes(vararg wordTypes: Int): List<Word> {
    val result = mutableListOf<Word>()
    for (word in this)
        if (wordTypes.contains(word.wordType))
            result.add(word)
    return result
}

fun List<Word>.filterByWordChar(vararg wordChars: Char): List<Word> {
    val result = mutableListOf<Word>()
    for (word in this)
        if (wordChars.contains(word.wordText[0]))
            result.add(word)
    return result
}

fun MutableList<Word>.removeQuestions(questionList: List<Question>) {
    for (question in questionList) removeAll { question.word.wordId == it.wordId }
}
//endregion WordListUtils