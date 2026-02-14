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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import com.tinygenius.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(
    levelId: Int,
    onNavigateBack: () -> Unit
) {

    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.win) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var completed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Puzzle Kids Dynu") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.puzzle1),
                contentDescription = "puzzle",
                modifier = Modifier
                    .size(260.dp)
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y

                            // 🎯 win condition center match
                            if (offsetX in -20f..20f && offsetY in -20f..20f && !completed) {
                                completed = true
                                mediaPlayer.start()
                            }
                        }
                    }
            )

            if (completed) {
                Text(
                    "🎉 Puzzle Completed!",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

