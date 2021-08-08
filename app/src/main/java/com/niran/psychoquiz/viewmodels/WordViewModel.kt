package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.repositories.WordRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WordViewModel(private val wordRepository: WordRepository) : ViewModel() {

    fun getAllWordsAsLiveData() = wordRepository.getAllWordsWithFlow().asLiveData()

    fun getWordsByLetterAsLiveData(firstLetter: Char) =
        wordRepository.getWordsByLetterWithFlow(firstLetter).asLiveData()

    fun customUpdateWord(word: Word, wordType: Word.Types) = viewModelScope.launch {
        val newType =
            if (word.wordType == wordType.ordinal) Word.Types.NEUTRAL
            else wordType
        wordRepository.deleteWord(word)
        delay(DELAY_TIME)
        wordRepository.insertWord(word.copy(wordType = newType.ordinal))
    }

    companion object {
        private const val DELAY_TIME = 20L
    }

}

class WordViewModelFactory(private val wordRepository: WordRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(wordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}