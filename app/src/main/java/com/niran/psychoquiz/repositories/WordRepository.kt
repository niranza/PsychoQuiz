package com.niran.psychoquiz.repositories

import com.niran.psychoquiz.database.daos.WordDao
import com.niran.psychoquiz.database.models.Word

class WordRepository(private val wordDao: WordDao) {

    suspend fun suspendedGetAllWords() = wordDao.suspendedGetAllWords()

    suspend fun suspendedGetAllTranslations() = wordDao.suspendedGetAllTranslations()

    fun getAllWords() = wordDao.getAllWords()

    fun getWordsByLetter(firstLetter: Char) = wordDao.getWordsByLetter(firstLetter)

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun updateWord(word: Word) = wordDao.updateWord(word)

    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)

}