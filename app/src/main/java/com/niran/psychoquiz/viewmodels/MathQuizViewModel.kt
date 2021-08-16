package com.niran.psychoquiz.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.niran.psychoquiz.database.models.settings.OutputOnePowerSetting
import com.niran.psychoquiz.database.models.settings.superclasses.getAllValid
import com.niran.psychoquiz.repositories.MathQuizSettingRepository
import com.niran.psychoquiz.utils.enums.LoadingState
import com.niran.psychoquiz.utils.enums.MathType
import kotlinx.coroutines.launch
import kotlin.math.pow


class MathQuizViewModel(private val repository: MathQuizSettingRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _outputOne = MutableLiveData<Int>()
    val outputOne: LiveData<String> = Transformations.map(_outputOne) { it.toString() }

    private val _outputTwo = MutableLiveData<Int>()
    val outputTwo: LiveData<String> = Transformations.map(_outputTwo) { it.toString() }

    private var outputOneList = listOf<Int>()
    private var outputTwoList = listOf<Int>()

    private var previousOutputs = mutableListOf<Int>()

    private var loopCount = 0

    init {
        _loadingState.value = LoadingState.LOADING
    }

    fun loadGame(mathType: MathType) = viewModelScope.launch {
        try {
            loadOutputs(mathType)
            loadNewMathQuestion(mathType)
            _loadingState.value = LoadingState.SUCCESS
        } catch (e: Exception) {
            _loadingState.value = LoadingState.ERROR.also { it.message = e.message.toString() }
        }
    }

    private suspend fun loadOutputs(mathType: MathType) {
        outputOneList = repository.getAllOutputOneSettings(mathType).getAllValid()
        outputTwoList = repository.getAllOutputTwoSettings(mathType).getAllValid()
    }

    fun loadNewMathQuestion(mathType: MathType) {
        if (!validSettings(mathType)) {
            _loadingState.value = LoadingState.ERROR.also { it.message = "Invalid Settings" }
            return
        }
        when (mathType) {
            MathType.MULTIPLICATION -> getRandomOutputArrays().also { randomArrays ->
                val currentOutputs =
                    mutableListOf(getOutput(randomArrays[0]), getOutput(randomArrays[1]))
                setOutputs(mathType, currentOutputs)
            }
            MathType.DIVISION -> getRandomOutputArrays().also { randomArrays ->
                val currentOutputs = mutableListOf(getOutput(randomArrays[0]))
                currentOutputs.add(0, currentOutputs[0].times(getOutput(randomArrays[1])))
                setOutputs(mathType, currentOutputs)
            }
            MathType.POWER -> {
                val currentOutputs = mutableListOf(getOutput(outputOneList))
                currentOutputs.add(
                    getOutput(OutputOnePowerSetting.Constant.getPower(currentOutputs[0]))
                )
                setOutputs(mathType, currentOutputs)
            }
        }
    }

    private fun getRandomOutputArrays(): List<List<Int>> =
        with(listOf(outputOneList, outputTwoList)) {
            val randomNum = (0..1).random()
            listOf(get(randomNum), get(if (randomNum == 1) 0 else 1))
        }

    private fun getOutput(list: List<Int>) = with(list) { get((0 until size).random()) }

    private fun setOutputs(mathType: MathType, currentOutputs: MutableList<Int>) {
        errorIf(mathType, currentOutputs == previousOutputs)
            .also { error -> if (error) return }
        _outputOne.value = currentOutputs[0]
        _outputTwo.value = currentOutputs[1]
        previousOutputs = currentOutputs
    }

    private fun errorIf(mathType: MathType, condition: Boolean) = when {
        loopCount > 5 -> {
            loopCount = 0
            false
        }
        condition -> {
            Log.d("MathQuizFragment", "looped $loopCount")
            loopCount++
            loadNewMathQuestion(mathType)
            true
        }
        else -> {
            loopCount = 0
            false
        }
    }

    private fun validSettings(mathType: MathType) = when (mathType) {
        MathType.POWER -> outputOneList.isNotEmpty()
        else -> outputOneList.isNotEmpty() && outputTwoList.isNotEmpty()
    }

    fun validateAnswer(mathType: MathType, answer: Int): Boolean = when (mathType) {
        MathType.MULTIPLICATION ->
            _outputOne.value?.times(_outputTwo.value ?: 0) ?: 0 == answer
        MathType.DIVISION ->
            _outputOne.value?.div(_outputTwo.value ?: 0) ?: 0 == answer
        MathType.POWER ->
            (_outputOne.value?.toDouble()?.pow(_outputTwo.value ?: 0) ?: 0).toInt() == answer
    }
}

class MathQuizViewModelFactory(
    private val repository: MathQuizSettingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MathQuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MathQuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}