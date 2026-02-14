package com.tinygenius.ui.coloring

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tinygenius.R

data class DrawPath(
    val path: Path,
    val color: Color,
    val width: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(pageId: Int, onNavigateBack: () -> Unit) {

    val context = LocalContext.current
    val clickSound = remember { MediaPlayer.create(context, R.raw.click) }

    var selectedColor by remember { mutableStateOf(Color.Red) }
    var paths by remember { mutableStateOf(listOf<DrawPath>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var strokeWidth by remember { mutableStateOf(12f) }
    var isEraser by remember { mutableStateOf(false) }

    val imageRes = when (pageId) {
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎨 Coloring Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                }
            )
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .background(Color(0xFFFFF8E1)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1f)
                    .background(Color.White)
            ) {

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {

                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                },
                                onDrag = { change, _ ->
                                    currentPath?.lineTo(
                                        change.position.x,
                                        change.position.y
                                    )
                                },
                                onDragEnd = {
                                    currentPath?.let {
                                        paths = paths + DrawPath(
                                            it,
                                            if (isEraser) Color.White else selectedColor,
                                            strokeWidth
                                        )
                                        currentPath = null
                                        clickSound.start()
                                    }
                                }
                            )
                        }
                ) {

                    paths.forEach {
                        drawPath(
                            path = it.path,
                            color = it.color,
                            style = Stroke(width = it.width)
                        )
                    }

                    currentPath?.let {
                        drawPath(
                            path = it,
                            color = if (isEraser) Color.White else selectedColor,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(onClick = {
                    isEraser = false
                    clickSound.start()
                }) { Text("Brush") }

                Button(onClick = {
                    isEraser = true
                    strokeWidth = 30f
                    clickSound.start()
                }) { Text("Eraser") }

                Button(onClick = {
                    if (paths.isNotEmpty()) {
                        paths = paths.dropLast(1)
                        clickSound.start()
                    }
                }) { Text("Undo") }

                Button(onClick = {
                    paths = emptyList()
                    clickSound.start()
                }) { Text("Clear") }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                listOf(
                    Color.Red,
                    Color.Blue,
                    Color.Green,
                    Color.Yellow,
                    Color.Magenta,
                    Color.Cyan,
                    Color.Black
                ).forEach { color ->

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .padding(4.dp)
                            .clickable {
                                selectedColor = color
                                isEraser = false
                                clickSound.start()
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { clickSound.release() }
    }
}

