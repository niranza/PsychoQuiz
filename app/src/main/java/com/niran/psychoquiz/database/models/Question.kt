package com.niran.psychoquiz.database.models

data class Question(

    var wordText: String = "",
    val correctAnswer: String = "",
    var answers: List<String> = listOf()

) {
    enum class Load { NEXT, PREVIOUS }
}
