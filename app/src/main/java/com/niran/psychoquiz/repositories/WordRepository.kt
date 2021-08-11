package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.WordDao
import com.niran.psychoquiz.database.models.Word

class WordRepository(private val wordDao: WordDao) {

    suspend fun getAllWords() = wordDao.getAllWords()

    suspend fun getAllTranslations() = wordDao.getAllTranslations()

    fun getAllWordsWithFlow() = wordDao.getAllWordsWithFlow()

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun updateWord(word: Word) = wordDao.updateWord(word)

    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)

}