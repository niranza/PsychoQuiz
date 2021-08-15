package com.niran.psychoquiz.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.superclasses.getAllValid
import com.niran.psychoquiz.repositories.QuestionRepository
import com.niran.psychoquiz.repositories.QuizSettingRepository
import com.niran.psychoquiz.repositories.WordRepository
import com.niran.psychoquiz.utils.*
import com.niran.psychoquiz.utils.enums.LoadingState
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

    private var totalNumberOfWords = 0
    val indexString get() = "${currentQuestionIndex + 1}/$totalNumberOfWords"
    val isCurrentIndexInitial get() = currentQuestionIndex == 0 || currentQuestionIndex == -1

    init {
        _loadingState.value = LoadingState.LOADING
    }

    fun loadGame(reloadQuiz: Boolean) = viewModelScope.launch {
//        try {
        if (reloadQuiz) {
            questionRepository.deleteAllQuestions()
            Log.d("TAG", "Deleted All Question")
        }
        updateQuestionList()
        Log.d("TAG", "Updated Question List Question")
        loadAnswerList()
        Log.d("TAG", "Loaded Answer List")
        loadWordList(getFirstLetterList(), getWordTypeList())
        Log.d("TAG", "Loaded Word List")
//        loadQuestionList()
//        Log.d("TAG", "Loaded Question List")
        validateLists().also { valid -> if (!valid) return@launch }
        Log.d("TAG", "Validated Lists")
        loadNewQuestion(Question.Load.NEXT)
        Log.d("TAG", "Loaded Next Question")
        _loadingState.value = LoadingState.SUCCESS
//        } catch (e: Exception) {
//            _loadingState.value = LoadingState.ERROR.apply {
//                if (!e.message.isNullOrBlank()) message = e.message.toString()
//            }
//        }
    }

    private suspend fun updateQuestionList() = withContext(Dispatchers.IO) {
        questionRepository.getAllQuestions().also { questionList = it as MutableList }
    }

    private suspend fun loadAnswerList() =
        wordRepository.getAllTranslations().also { answerList = it }

    private suspend fun getFirstLetterList(): CharArray = withContext(Dispatchers.IO) {
        quizSettingRepository.getAllWordFirstLetterSettings().getAllValid<Char>().toCharArray()
    }

    private suspend fun getWordTypeList(): IntArray = withContext(Dispatchers.IO) {
        quizSettingRepository.getAllWordTypeSettings().getAllValid<Int>().toIntArray()
    }

    private suspend fun loadWordList(wordFirstLetters: CharArray, wordTypes: IntArray) =
        withContext(Dispatchers.IO) {
            val list = wordRepository.getAllWords().filterByWordChar(*wordFirstLetters)
                .filterByWordTypes(*wordTypes).shuffled() as MutableList
            Log.d("TAG", "Word List is with size of ${list.size}")
            wordList = list
        }

    private suspend fun loadQuestionList() =
        withContext(Dispatchers.Default) { questionList.filterByWords(*wordList.toTypedArray()) }

    private suspend fun validateLists(): Boolean = withContext(Dispatchers.Main) {
        if (wordList.isEmpty()) {
            Log.d("TAG", "Word List is Empty. Error will be called")
            LoadingState.ERROR.message = INVALID_SETTINGS
            _loadingState.value = LoadingState.ERROR
            return@withContext false
        } else totalNumberOfWords = wordList.size

        currentQuestionIndex = if (questionList.isEmpty()) -1 else {
            Log.d("TAG", "Question List wasn't empty. Updating data")
            wordList.removeQuestions(questionList)
            questionList.size - 2
        }
        true
    }

    fun loadNewQuestion(loadState: Question.Load) = viewModelScope.launch {
        when (loadState) {
            Question.Load.NEXT -> currentQuestionIndex++
            Question.Load.PREVIOUS -> currentQuestionIndex--
        }
        if (currentQuestionIndex >= questionList.size) addQuestion().also { cannotAddQuestion ->
            if (!cannotAddQuestion) currentQuestionIndex--
        }
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
        if (_question.value?.questionId == newQuestion.questionId) _question.value = newQuestion
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