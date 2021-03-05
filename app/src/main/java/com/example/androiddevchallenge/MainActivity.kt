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
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                TimerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TimerApp(viewModel: MainViewModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        BoxCountDownProgress(viewModel, Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            IconButton(onClick = {
                viewModel.pauseTimer()
            }) {
                Icon(imageVector = Icons.Filled.Pause, contentDescription = null)
            }

            IconButton(onClick = {
                viewModel.startTimer()
            }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
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
                .height(300.dp)
                .aspectRatio(1f)
                .align(Alignment.Center)
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = viewModel.timerText,
                style = MaterialTheme.typography.h2,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}