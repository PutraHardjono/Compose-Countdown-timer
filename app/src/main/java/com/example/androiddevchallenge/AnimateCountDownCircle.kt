package com.example.androiddevchallenge

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

enum class Progress { START, END }

@Composable
fun AnimateCountDownCircle(
    countDownMilli: Int,
    transition: Transition<Progress>,
    modifier: Modifier = Modifier
) {
//    var currentState by remember { mutableStateOf(Progress.START) }
//    val transition = updateTransition(targetState = currentState)

    val angleOffSet by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = countDownMilli,
                easing = LinearEasing
            )
        }
    ) {
        when (it) {
            Progress.START -> 0f
            Progress.END -> -360f
        }
    }

    val stroke = with(LocalDensity.current) { Stroke(10.dp.toPx()) }
    val strokeEraser = with(LocalDensity.current) { Stroke(11.dp.toPx()) }

    Canvas(modifier = modifier) {
        Log.d("AnimateCountDownCircleCanvas", "progress: ${transition.currentState}")
        val innerRadius = (size.minDimension - stroke.width) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        val startAngle = -90f

        drawArc(
            color = Color.Blue,
            startAngle = startAngle,
            sweepAngle = 360f,
            topLeft = topLeft,
            size = size,
            useCenter = true,
            style = stroke
        )

        if (transition.isRunning || transition.currentState == Progress.END) {
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = angleOffSet,
                topLeft = topLeft,
                size = size,
                useCenter = true,
                style = strokeEraser
            )
        }
        Log.d("AnimateCountDownCircleCanvas", "angleOffSet: $angleOffSet")
    }
}