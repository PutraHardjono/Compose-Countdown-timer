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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme(true) {
                TimerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TimerApp(viewModel: MainViewModel) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            when (viewModel.state) {
                State.SETTING -> BoxSettingTime(viewModel, Modifier.align(Alignment.CenterHorizontally))
                State.PAUSE, State.IN_PROGRESS -> BoxCountDownProgress(
                    viewModel,
                    Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetTimer()
                    },
                    enabled = viewModel.isStopButtonEnabled()
                ) {
                    Icon(imageVector = Icons.Filled.Stop, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = {
                        when (viewModel.state) {
                            State.IN_PROGRESS -> viewModel.pauseTimer()
                            State.SETTING, State.PAUSE -> viewModel.startTimer()
                        }
                    },
                    enabled = viewModel.isPlayButtonEnabled()
                ) {
                    when (viewModel.state) {
                        State.IN_PROGRESS -> Icon(imageVector = Icons.Filled.Pause, contentDescription = null)
                        State.SETTING, State.PAUSE -> Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxCountDownProgress(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        CircularProgressIndicator(
            progress = viewModel.progress,
            strokeWidth = 13.dp,
            modifier = Modifier
                .height(250.dp)
                .aspectRatio(1f)
                .align(Alignment.Center)
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = viewModel.timerText,
                style = MaterialTheme.typography.h3,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun BoxSettingTime(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val focusRequesterMinutes = remember { FocusRequester() }
    val focusRequesterHours = remember { FocusRequester() }

    Box(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(250.dp)) {
            OutlinedTextField(
                value = if (viewModel.hours == 0L) "" else viewModel.hours.toString(),
                onValueChange = viewModel::setHours,
                placeholder = { Text(stringResource(id = R.string.hours)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusRequesterMinutes.requestFocus()
                }),
                modifier = modifier.width(100.dp)
            )

            Text(text = ":", modifier = modifier.padding(start = 8.dp, end = 8.dp))

            OutlinedTextField(
                value = if (viewModel.minutes == 0L) "" else viewModel.minutes.toString(),
                onValueChange = viewModel::setMinutes,
                placeholder = { Text(stringResource(id = R.string.minutes)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusRequesterHours.requestFocus()
                }),
                modifier = modifier.width(100.dp).focusRequester(focusRequesterMinutes)
            )

            Text(text = ":", modifier = modifier.padding(start = 8.dp, end = 8.dp))

            OutlinedTextField(
                value = if (viewModel.seconds == 0L) "" else viewModel.seconds.toString(),
                onValueChange = viewModel::setSeconds,
                placeholder = { Text(stringResource(id = R.string.seconds)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (viewModel.state == State.SETTING) viewModel.startTimer()
                }),
                modifier = modifier.width(100.dp).focusRequester(focusRequesterHours)
            )
        }
    }
}
