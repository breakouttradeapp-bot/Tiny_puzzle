package com.tinygenius.ui.puzzle

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tinygenius.R
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(levelId: Int, onNavigateBack: () -> Unit) {

    val context = LocalContext.current
    val winSound = remember { MediaPlayer.create(context, R.raw.win) }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFFFDEE9), Color(0xFFB5FFFC))
    )

    var completed by remember { mutableStateOf(false) }

    val correctX = 300f
    val correctY = 500f

    var offsetX by remember { mutableStateOf(50f) }
    var offsetY by remember { mutableStateOf(50f) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🧩 Puzzle Level $levelId") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            // target
            Box(
                modifier = Modifier
                    .offset { IntOffset(correctX.toInt(), correctY.toInt()) }
                    .size(160.dp)
                    .background(Color.White.copy(0.4f))
            )

            Image(
                painter = painterResource(id = R.drawable.puzzle1),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
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
                    "🎉 Level Completed!",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

