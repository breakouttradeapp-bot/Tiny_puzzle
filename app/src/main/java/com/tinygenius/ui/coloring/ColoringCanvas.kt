package com.tinygenius.ui.coloring

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
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
    var brushSize by remember { mutableStateOf(18f) }
    var eraser by remember { mutableStateOf(false) }

    val imageRes = when (pageId) {
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }

    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color.Magenta, Color.Cyan, Color.Black, Color.Gray
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎨 Coloring Game") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF3E0))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.White)
            ) {

                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
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
                                            if (eraser) Color.White else selectedColor,
                                            brushSize
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
                            style = Stroke(it.width)
                        )
                    }

                    currentPath?.let {
                        drawPath(
                            path = it,
                            color = if (eraser) Color.White else selectedColor,
                            style = Stroke(brushSize)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()) {

                IconButton(onClick = {
                    eraser = false
                    clickSound.start()
                }) {
                    Icon(Icons.Default.Create, "brush", tint = Color.Blue)
                }

                IconButton(onClick = {
                    eraser = true
                    brushSize = 40f
                    clickSound.start()
                }) {
                    Icon(Icons.Default.Clear, "eraser", tint = Color.Red)
                }

                IconButton(onClick = {
                    if (paths.isNotEmpty()) {
                        paths = paths.dropLast(1)
                        clickSound.start()
                    }
                }) {
                    Icon(Icons.Default.Refresh, "undo")
                }

                IconButton(onClick = {
                    paths = emptyList()
                    clickSound.start()
                }) {
                    Icon(Icons.Default.Delete, "clear")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .clickable {
                                selectedColor = color
                                eraser = false
                                clickSound.start()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                val win = MediaPlayer.create(context, R.raw.win)
                win.start()
            }) {
                Text("🎉 Finished")
            }
        }
    }
}

