package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.*
import com.niran.psychoquiz.LoadingState
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.repositories.QuestionRepository
import com.niran.psychoquiz.repositories.QuizSettingRepository
import com.niran.psychoquiz.repositories.WordRepository
import com.niran.psychoquiz.utils.filterByWordChar
import com.niran.psychoquiz.utils.filterByWordTypes
import com.niran.psychoquiz.utils.removeQuestions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizViewModel(
    private val wordRepository: WordRepository,
    private val quizSettingRepository: QuizSettingRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> get() = _question

    private val _eventQuizFinished = MutableLiveData(false)
    val eventQuizFinished: LiveData<Boolean> get() = _eventQuizFinished

    private var currentQuestionIndex = -1
    private var questionList = mutableListOf<Question>()
    private var wordList = mutableListOf<Word>()
    private var answerList = listOf<String>()

    val indexString get() = "${currentQuestionIndex + 1}/${questionList.size + wordList.size}"
    val isCurrentIndexInitial get() = currentQuestionIndex == 0 || currentQuestionIndex == -1

    var reloadQuiz = true

    init {
        loadGame()
    }

    private fun loadGame() = viewModelScope.launch {
        try {
            _loadingState.value = LoadingState.LOADING
            loadAnswerList()
            loadWordList(getFirstLetterList(), getWordTypeList())
            loadNewQuestion(Question.Load.NEXT)
            _loadingState.value = LoadingState.SUCCESS
        } catch (e: Exception) {
            _loadingState.value = LoadingState.ERROR.apply {
                if (e.message.isNullOrBlank()) message = e.message.toString()
            }
        }
    }

    private suspend fun getFirstLetterList(): CharArray =
        withContext(Dispatchers.IO) {
            Word.FirstLetter.getAllValid(
                quizSettingRepository.getAllWordFirstLetterSettings()
            ).toCharArray()
        }

    private suspend fun getWordTypeList(): IntArray =
        withContext(Dispatchers.IO) {
            Word.Types.UNKNOWN.getAllValid(
                quizSettingRepository.getAllWordTypeSettings()
            ).toIntArray()
        }

    private suspend fun loadWordList(wordFirstLetters: CharArray, wordTypes: IntArray) =
        wordRepository.getAllWords().also { _wordList ->
            withContext(Dispatchers.IO) {

                val list = _wordList.filterByWordChar(*wordFirstLetters)
                    .filterByWordTypes(*wordTypes).shuffled() as MutableList

                if (list.isEmpty()) {
                    LoadingState.ERROR.message = INVALID_SETTINGS
                    _loadingState.value = LoadingState.ERROR
                    return@withContext
                }

                if (!reloadQuiz)
                    questionRepository.getAllQuestions().also {
                        if (it.isNotEmpty()) {
                            questionList = it as MutableList<Question>
                            currentQuestionIndex = it.size - 2
                            list.removeQuestions(it)
                        }
                    } else questionRepository.deleteAllQuestions()

                wordList = list
            }
        }

    private suspend fun loadAnswerList() =
        wordRepository.getAllTranslations().also { answerList = it }

    fun loadNewQuestion(loadState: Question.Load) {
        when (loadState) {
            Question.Load.NEXT -> {
                currentQuestionIndex++
                if (currentQuestionIndex == questionList.size)
                    addQuestion().also { cannotAddQuestion -> if (!cannotAddQuestion) return }
            }
            Question.Load.PREVIOUS -> currentQuestionIndex--
        }
        _question.value = questionList[currentQuestionIndex]
    }

    private fun addQuestion(): Boolean {
        if (wordList.isEmpty()) _eventQuizFinished.apply {
            value = true
            value = false
            return false
        }
        val word = getWord()
        val answers = (getAnswers(word.wordTranslation)).shuffled()
        Question(
            wordId = word.wordId,
            questionWordText = word.wordText,
            correctAnswer = word.wordTranslation,
            answers = answers
        ).also {
            insertQuestion(it)
            questionList.add(it)
        }
        return true
    }

    private fun getWord(): Word = with(wordList) { get(0).also { remove(it) } }

    private fun getAnswers(correctAnswer: String): List<String> = with(mutableListOf<String>()) {
        while (count() < NUMBER_OF_MISLEADING_ANSWERS) {
            answerList[(answerList.indices).random()].also { randomAnswer ->
                if (randomAnswer != correctAnswer) add(randomAnswer)
            }
        }

        this + correctAnswer
    }

    private fun insertQuestion(question: Question) =
        viewModelScope.launch { questionRepository.insertQuestion(question) }

    fun assertButtonAnswer(buttonText: String): Boolean =
        buttonText == questionList[currentQuestionIndex].correctAnswer

    companion object {
        private const val NUMBER_OF_MISLEADING_ANSWERS = 3

        const val INVALID_SETTINGS = "Invalid Settings"
    }
}

class QuizViewModelFactory(
    private val wordRepository: WordRepository,
    private val quizSettingRepository: QuizSettingRepository,
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(wordRepository, quizSettingRepository, questionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}