package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.*
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.superclasses.getAllValid
import com.niran.psychoquiz.repositories.QuestionRepository
import com.niran.psychoquiz.repositories.QuizSettingRepository
import com.niran.psychoquiz.repositories.WordRepository
import com.niran.psychoquiz.utils.QuizAiUtil
import com.niran.psychoquiz.utils.enums.LoadingState
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
    private var questionList = listOf<Question>()
    private var wordList = mutableListOf<Word>()
    private var answerList = listOf<String>()

    private var totalNumberOfQuestion = 0
    val indexString get() = "${currentQuestionIndex + 1}/$totalNumberOfQuestion"
    val isCurrentIndexInitial get() = currentQuestionIndex == 0 || currentQuestionIndex == -1

    init {
        _loadingState.value = LoadingState.LOADING
    }

    fun loadGame(reloadQuiz: Boolean) = viewModelScope.launch {
        try {
            if (reloadQuiz) questionRepository.deleteAllQuestions()
            updateQuestionList()
            loadAnswerList()
            loadWordList(getFirstLetterList(), getWordTypeList())
            validateWordList()
            loadNewQuestion(Question.Load.NEXT)
            _loadingState.value = LoadingState.SUCCESS
        } catch (e: Exception) {
            _loadingState.value = LoadingState.ERROR.apply {
                if (e.message.isNullOrBlank()) message = e.message.toString()
            }
        }
    }

    private suspend fun updateQuestionList() =
        questionRepository.getAllQuestions().also { questionList = it }

    private suspend fun loadAnswerList() =
        wordRepository.getAllTranslations().also { answerList = it }

    private suspend fun getFirstLetterList(): CharArray =
        withContext(Dispatchers.IO) {
            quizSettingRepository.getAllWordFirstLetterSettings().getAllValid<Char>().toCharArray()
        }

    private suspend fun getWordTypeList(): IntArray =
        withContext(Dispatchers.IO) {
            quizSettingRepository.getAllWordTypeSettings().getAllValid<Int>().toIntArray()
        }

    private suspend fun loadWordList(wordFirstLetters: CharArray, wordTypes: IntArray) =
        withContext(Dispatchers.IO) {
            val list = wordRepository.getAllWords().filterByWordChar(*wordFirstLetters)
                .filterByWordTypes(*wordTypes).shuffled() as MutableList
            wordList = list
        }

    private suspend fun validateWordList() = withContext(Dispatchers.Default) {
        if (wordList.isEmpty()) {
            LoadingState.ERROR.message = INVALID_SETTINGS
            _loadingState.value = LoadingState.ERROR
            return@withContext
        } else totalNumberOfQuestion = wordList.size

        currentQuestionIndex = if (questionList.isEmpty()) -1 else {
            wordList.removeQuestions(questionList)
            questionList.size - 2
        }
    }

    fun loadNewQuestion(loadState: Question.Load) = viewModelScope.launch {
        when (loadState) {
            Question.Load.NEXT -> currentQuestionIndex++
            Question.Load.PREVIOUS -> currentQuestionIndex--
        }
        if (currentQuestionIndex >= questionList.size)
            addQuestion().also { cannotAddQuestion -> if (!cannotAddQuestion) return@launch }
        _question.value = questionList[currentQuestionIndex]
    }

    private suspend fun addQuestion(): Boolean {
        if (wordList.isEmpty()) _eventQuizFinished.apply {
            value = true
            value = false
            return false
        }
        val word = getWord()
        val answers = (getAnswers(word.wordTranslation)).shuffled()
        Question(word = word, answers = answers).also {
            questionRepository.insertQuestion(it)
            updateQuestionList()
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

    fun assertButtonAnswer(buttonText: String): Boolean =
        buttonText == questionList[currentQuestionIndex].word.wordTranslation

    //region QuizAi
    private val _eventShowDialog = MutableLiveData<Boolean>()
    val eventShowDialog get() = _eventShowDialog

    private lateinit var newQuestion: Question
    val wordText get() = newQuestion.word.wordText

    fun loadNewWord(wrongAnswerCount: Int) =
        QuizAiUtil.getNewWord(wrongAnswerCount, _question.value!!).also {
            newQuestion = it.newQuestion
            if (it.showDialog) _eventShowDialog.apply {
                value = true
                value = false
                return@also
            }
            updateCurrentQuestion()
        }

    fun updateCurrentQuestion() = viewModelScope.launch {
        questionRepository.updateQuestion(newQuestion)
        wordRepository.updateWord(newQuestion.word)
        updateQuestionList()
    }

    //endregion QuizAi

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