package com.tinygenius.ui.puzzle

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.tinygenius.R
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(levelId: Int, onNavigateBack: () -> Unit) {

    val context = LocalContext.current
    val winSound = remember { MediaPlayer.create(context, R.raw.win) }

    // grid size by level
    val grid = when(levelId){
        1 -> 2
        2 -> 3
        else -> 4
    }

    val pieceSize = 120
    val boardSize = grid * pieceSize

    // correct positions
    val correctPositions = remember {
        List(grid*grid){
            Pair((it%grid)*pieceSize.toFloat(),
                 (it/grid)*pieceSize.toFloat())
        }
    }

    // current positions shuffled
    val positions = remember {
        mutableStateListOf<Offset>().apply {
            repeat(grid*grid){
                add(Offset((50..500).random().toFloat(),
                           (50..900).random().toFloat()))
            }
        }
    }

    var completed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🧩 Level $levelId") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            // board area
            Box(
                modifier = Modifier
                    .size(boardSize.dp)
                    .border(3.dp, Color.Black)
            )

            // pieces
            for(i in positions.indices){

                var offset by remember { mutableStateOf(positions[i]) }

                Image(
                    painter = painterResource(id = R.drawable.puzzle1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(pieceSize.dp)
                        .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                        .pointerInput(Unit){
                            detectDragGestures(
                                onDragEnd = {

                                    val correct = correctPositions[i]

                                    if(abs(offset.x - correct.first) < 80 &&
                                       abs(offset.y - correct.second) < 80){
                                        offset = Offset(correct.first, correct.second)
                                        positions[i] = offset

                                        if(positions == correctPositions.map{
                                                Offset(it.first,it.second)
                                            }){
                                            completed = true
                                            winSound.start()
                                        }
                                    }
                                }
                            ){ change, drag ->
                                change.consume()
                                offset += drag
                                positions[i] = offset
                            }
                        }
                )
            }

            if(completed){
                Text(
                    "🎉 Puzzle Completed!",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

