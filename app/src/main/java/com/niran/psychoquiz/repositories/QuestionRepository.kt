package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.QuestionDao
import com.niran.psychoquiz.database.models.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    suspend fun getAllQuestions() = questionDao.getAllQuestions()

    suspend fun deleteAllQuestions() = questionDao.deleteAllQuestions()

    suspend fun insertQuestion(question: Question) = questionDao.insertQuestion(question)

    suspend fun updateQuestion(question: Question) = questionDao.updateQuestion(question)
}