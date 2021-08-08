package com.niran.psychoquiz.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.niran.psychoquiz.LoadingState


class MultiplicationViewModel : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _outputOne = MutableLiveData<Int>()
    val outputOne: LiveData<String> = Transformations.map(_outputOne) { it.toString() }

    private val _outputTwo = MutableLiveData<Int>()
    val outputTwo: LiveData<String> = Transformations.map(_outputTwo) { it.toString() }

    var outputOneArray = IntArray(0)
    var outputTwoArray = IntArray(0)

    init {
        _loadingState.value = LoadingState.LOADING
    }

    fun restart() = loadGame()

    private fun loadGame() {
        Log.d("TAG", "Start loading game")
        if (!validSettings()) {
            _loadingState.value = LoadingState.ERROR.also { it.message = "Invalid Settings" }
            return
        }
        _outputOne.value = getOutput(outputOneArray)
        _outputTwo.value = getOutput(outputTwoArray)
        _loadingState.value = LoadingState.SUCCESS
    }

    private fun getOutput(arr: IntArray) = with(arr) { get((0 until size).random()) }

    private fun validSettings() = outputOneArray.isNotEmpty() && outputTwoArray.isNotEmpty()

    fun validateAnswer(answer: Int) =
        _outputOne.value?.times(_outputTwo.value ?: 0) ?: 0 == answer
}

//class MultiplicationViewModelFactory : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MultiplicationViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return MultiplicationViewModel() as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel Class")
//    }
//}