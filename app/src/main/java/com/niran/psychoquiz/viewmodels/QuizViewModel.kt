package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.*
import com.niran.psychoquiz.LoadingState
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.repositories.WordRepository
import com.niran.psychoquiz.utils.filterByWordChar
import com.niran.psychoquiz.utils.filterByWordTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizViewModel(
    private val wordRepository: WordRepository,
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> get() = _question

    private var currentWordIndex = -1
    private var questionList = mutableListOf<Question>()
    private var wordList = listOf<Word>()
    private var answerList = listOf<String>()

    val indexString get() = "${currentWordIndex.plus(1)}/${wordList.size}"
    val isCurrentIndexInitial get() = currentWordIndex == 0 || currentWordIndex == -1

    //for testing
    private val alphabet = ('a'..'z').toList().toCharArray()
    private val wordTypes =
        intArrayOf(
            Word.Types.UNKNOWN.ordinal,
            Word.Types.KNOWN.ordinal,
            Word.Types.FAVORITE.ordinal,
            Word.Types.NEUTRAL.ordinal
        )

    init {
        //the settings data here will be loaded from the database
        _question.value = Question()
        loadGame()
    }

    private fun loadGame() = viewModelScope.launch {
        try {
            _loadingState.value = LoadingState.LOADING
            loadWordList(getFirstLetterList(), getWordTypeList())
            loadAnswerList()
            loadNewQuestion(Question.Load.NEXT)
            _loadingState.value = LoadingState.SUCCESS
        } catch (e: Exception) {
            LoadingState.ERROR.message = e.message.toString()
            _loadingState.value = LoadingState.ERROR
        }
    }

    private suspend fun getFirstLetterList(): CharArray =
        withContext(Dispatchers.IO) { Word.FirstLetter.A.getAllValid().toCharArray() }

    private suspend fun getWordTypeList(): IntArray =
        withContext(Dispatchers.IO) { Word.Types.UNKNOWN.getAllValid().toIntArray() }

    private suspend fun loadWordList(wordFirstLetters: CharArray, wordTypes: IntArray) =
        wordRepository.suspendedGetAllWords().also { _wordList ->
            if (wordFirstLetters.isEmpty() || wordTypes.isEmpty()) {
                LoadingState.ERROR.message = INVALID_SETTINGS
                _loadingState.value = LoadingState.ERROR
                return@also
            }
            withContext(Dispatchers.IO) {
                val list =
                    _wordList.filterByWordChar(*wordFirstLetters).filterByWordTypes(*wordTypes)
                        .shuffled()
//                Log.d("TAG", "list size before distinct: ${list.count()}")
//                Log.d("TAG", "list size after distinct: ${list.distinctBy { it.wordTranslation }.count()}")
                wordList = list
            }
        }

    private suspend fun loadAnswerList() =
        wordRepository.suspendedGetAllTranslations().also { answerList = it }

    private fun addQuestion(index: Int) {
        val word = getWord(index)
        val answers = (getAnswers(word.wordTranslation)).shuffled()
        questionList.add(Question(word.wordText, word.wordTranslation, answers))
    }

    private fun getWord(index: Int): Word = wordList[index]

    private fun getAnswers(correctAnswer: String): List<String> {
        val resultList = mutableListOf<String>()
        var addedAnswers = 0
        while (addedAnswers < NUMBER_OF_MISLEADING_ANSWERS) {
            val randomAnswer = answerList[(answerList.indices).random()]
            if (randomAnswer != correctAnswer && addedAnswers < 3) {
                resultList.add(randomAnswer)
                addedAnswers++
            }
        }
        return resultList + correctAnswer
    }

    fun loadNewQuestion(loadState: Question.Load) {
        when (loadState) {
            Question.Load.NEXT -> {
                currentWordIndex++
                addQuestion(currentWordIndex)
            }
            Question.Load.PREVIOUS -> currentWordIndex--
        }
        _question.value = questionList[currentWordIndex]
    }

    fun assertButtonAnswer(buttonText: String): Boolean =
        buttonText == questionList[currentWordIndex].correctAnswer

    companion object {
        private const val NUMBER_OF_MISLEADING_ANSWERS = 3

        const val INVALID_SETTINGS = "Invalid Settings"
    }
}

class QuizViewModelFactory(
    private val wordRepository: WordRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(wordRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}