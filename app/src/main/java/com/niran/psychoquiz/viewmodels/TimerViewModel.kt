package com.niran.psychoquiz.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.niran.psychoquiz.utils.MINUETS_IN_MILLIS
import com.niran.psychoquiz.utils.SECONDS_IN_MILLIS

class TimerViewModel : ViewModel() {

    private lateinit var countDownTimer: CountDownTimer

    private val _timeLeftInMillis = MutableLiveData(ESSAY_IN_MILLIS)
    val timeLeftInMillis: LiveData<String>
        get() = Transformations.map(_timeLeftInMillis) {
            val timeLeft = it
            val minuets = (timeLeft / MINUETS_IN_MILLIS).toInt()
            val seconds = (timeLeft / SECONDS_IN_MILLIS % 60).toInt()
            String.format("%02d:%02d", minuets, seconds)
        }

    private val _chapter = MutableLiveData(0)
    val chapter: LiveData<Int> = _chapter

    private val _eventTimerFinished = MutableLiveData<Boolean>()
    val eventTimerFinished: LiveData<Boolean> get() = _eventTimerFinished

    private var _timerRunning = false
    val timerRunning get() = _timerRunning

    fun startTimer(totalNumberOfChapters: Int) {

        countDownTimer = getTimer(totalNumberOfChapters)

        _timerRunning = true
    }

    fun pauseTimer() {
        countDownTimer.cancel()
        _timerRunning = false
    }

    fun resetTimer(essay: Boolean) = _chapter.apply {
        _timeLeftInMillis.value = if (essay) {
            value = 0
            ESSAY_IN_MILLIS
        } else {
            value = 1
            SINGLE_CHAPTER_IN_MILLIS
        }
    }

    private fun getTimer(totalNumberOfChapters: Int) =
        object : CountDownTimer(_timeLeftInMillis.value ?: 0, SECONDS_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                if (_chapter.value ?: 0 == totalNumberOfChapters) _eventTimerFinished.apply {
                    _timerRunning = false
                    value = true
                    value = false
                    return
                }
                _chapter.value = _chapter.value?.plus(1)
                _timeLeftInMillis.value = SINGLE_CHAPTER_IN_MILLIS
                startTimer(totalNumberOfChapters)
            }
        }.start()

    companion object {
        const val ESSAY_IN_MILLIS = 30 * MINUETS_IN_MILLIS
        const val SINGLE_CHAPTER_IN_MILLIS = 20 * MINUETS_IN_MILLIS
    }
}

class TimerViewModelFactory