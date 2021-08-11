package com.niran.psychoquiz.utils

import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word

//region WordListUtils
fun List<Word>.sortByType() = sortedBy { it.wordType }

fun List<Word>.filterWordListBySearchQuery(searchQuery: String?) =
    filter {
        it.wordText.replace("\\s".toRegex(), "")
            .contains(searchQuery ?: "", true)
                || it.wordTranslation.replace("\\s".toRegex(), "")
            .contains(searchQuery ?: "", true)
    }

fun List<Word>.filterByWordTypes(vararg foodTypes: Int): List<Word> {
    val result = mutableListOf<Word>()
    for (word in this)
        for (wordType in foodTypes)
            if (word.wordType == wordType) {
                result.add(word)
                break;
            }
    return result
}

fun List<Word>.filterByWordChar(vararg wordChars: Char): List<Word> {
    val result = mutableListOf<Word>()
    for (word in this)
        for (wordChar in wordChars)
            if (word.wordText[0] == wordChar) {
                result.add(word)
                break;
            }
    return result
}

fun MutableList<Word>.removeQuestions(questionList: List<Question>) {
    for (question in questionList) removeAll { question.word.wordId == it.wordId }
}
//endregion WordListUtils