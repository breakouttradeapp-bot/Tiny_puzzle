package com.tinygenius.ui.puzzle

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalContext
import com.tinygenius.R
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(
    levelId: Int,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val winSound = remember { MediaPlayer.create(context, R.raw.win) }

    var completed by remember { mutableStateOf(false) }

    // correct position
    val correctX = 200f
    val correctY = 300f

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Puzzle Level 1") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            // target area
            Box(
                modifier = Modifier
                    .offset { IntOffset(correctX.toInt(), correctY.toInt()) }
                    .size(150.dp)
                    .background(Color.LightGray)
            )

            // draggable puzzle piece
            Image(
                painter = painterResource(id = R.drawable.puzzle1),
                contentDescription = "puzzle",
                modifier = Modifier
                    .size(150.dp)
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {

                                if (abs(offsetX - correctX) < 80 &&
                                    abs(offsetY - correctY) < 80
                                ) {
                                    offsetX = correctX
                                    offsetY = correctY
                                    completed = true
                                    winSound.start()
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            )

            if (completed) {
                Text(
                    "🎉 Puzzle Completed!",
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = Color.Blue,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

