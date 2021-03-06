/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

private const val COUNT_DOWN_INTERVAL = 8L

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

    // Progress for CircularProgressIndicator
    var progress by mutableStateOf(0f)
        private set

    fun isStopButtonEnabled(): Boolean {
        return state == State.IN_PROGRESS || state == State.PAUSE
    }

    fun isPlayButtonEnabled(): Boolean {
        return when {
            state == State.SETTING && (hours != 0L || minutes != 0L || seconds != 0L) -> true
            state == State.IN_PROGRESS || state == State.PAUSE -> true
            else -> false
        }
    }

    // totalMilliSecond with value being updated
    private var totalMilli = 0L

    // totalMilliSecond from user input
    private var _totalMilliSet = 0L
    private var timer: CountDownTimer? = null

    fun setHours(text: String) {
        hours = text.toLongOrNull() ?: 0
    }

    fun setMinutes(text: String) {
        minutes = checkMinuteSecond(text)
    }

    fun setSeconds(text: String) {
        seconds = checkMinuteSecond(text)
    }

    // check input for Minute and Second
    private fun checkMinuteSecond(text: String): Long {
        val value = text.toLongOrNull() ?: 0

        return when {
            value >= 100 -> text.take(2).toLongOrNull() ?: 0
            value >= 60 -> text.take(1).toLongOrNull() ?: 0
            value < 0 -> 0
            else -> value
        }
    }

    private fun setTotalMilli() {
        totalMilli =
            TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(
            seconds
        )
        _totalMilliSet = totalMilli
    }

    fun startTimer() {
        when (state) {
            State.SETTING -> {
                setTotalMilli()
                if (totalMilli == 0L) return
            }
            State.IN_PROGRESS -> return
            else -> {
            }
        }

        if (totalMilli == 0L) totalMilli = _totalMilliSet

        timer = object : CountDownTimer(totalMilli, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                totalMilli = millisUntilFinished
                progress = totalMilli.toFloat() / _totalMilliSet
                updateText(millisUntilFinished)
            }

            override fun onFinish() {
                totalMilli = 0
                progress = 0f
                updateText(0L)
                state = State.PAUSE // TODO
                timer?.cancel()
            }
        }
        timer?.let {
            it.start()
            state = State.IN_PROGRESS
        }
    }

    fun pauseTimer() {
        timer?.let {
            it.cancel()
            state = State.PAUSE
        }
    }

    fun resetTimer() {
        timer?.cancel()
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
