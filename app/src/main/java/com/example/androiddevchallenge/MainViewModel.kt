package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    var hours by mutableStateOf(0L)
        private set

    var minutes by mutableStateOf(0L)
        private set

    var seconds by mutableStateOf(0L)
        private set

    var timerText by mutableStateOf("00:00:00")
        private set

    var state by mutableStateOf(State.SETTING)
        private set

    var progress by mutableStateOf(0f)
        private set

    private var totalMilli = 36000L //TODO
    private var _totalMilliSet = 36000L //TODO
    private var timer: CountDownTimer? = null

    fun setTimer() {
        totalMilli =
            TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(
                seconds
            )
        _totalMilliSet = totalMilli
    }

    fun startTimer() {
        if (state == State.IN_PROGRESS) return

        Log.d("MainViewModel", "startTimer()")
        timer = object : CountDownTimer(totalMilli, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("MainViewModel", "startTimer - until finish: $millisUntilFinished")
                totalMilli = millisUntilFinished
                progress = totalMilli.toFloat() / _totalMilliSet
                updateText(millisUntilFinished)
            }

            override fun onFinish() {
                totalMilli = 0
                progress = 0f
                updateText(0L)
                state = State.PAUSE // TODO
            }
        }
        timer?.let {
            it.start()
            state = State.IN_PROGRESS
        }
    }

    fun pauseTimer() {
        Log.d("MainViewModel", "pauseTimer(): $timer")
        timer?.let {
            it.cancel()
            state = State.PAUSE
        }
    }

    fun resetTimer() {
        state = State.SETTING
    }

    private fun updateText(millisUntilFinished: Long) {
        timerText = String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1)
        )
    }
}

enum class State {
    SETTING, IN_PROGRESS, PAUSE
}