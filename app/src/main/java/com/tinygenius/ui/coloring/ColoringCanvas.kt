package com.tinygenius.ui.coloring

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tinygenius.R

data class PathData(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(pageId: Int, onNavigateBack: () -> Unit) {

    val context = LocalContext.current

    // SAFE SOUND
    val clickSound = remember {
        MediaPlayer.create(context, R.raw.click).apply { isLooping = false }
    }

    var selectedColor by remember { mutableStateOf(Color.Red) }
    var paths by remember { mutableStateOf(listOf<PathData>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var strokeWidth by remember { mutableStateOf(18f) }
    var isEraser by remember { mutableStateOf(false) }

    val imageRes = when (pageId) {
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }

    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color.Magenta, Color.Cyan, Color(0xFFFF9800),
        Color(0xFF795548), Color.Black, Color.Gray,
        Color(0xFF6200EA), Color(0xFF00C853)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎨 Kids Coloring", fontWeight = FontWeight.Bold) },
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
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFf6d365), Color(0xFFfda085))
                    )
                )
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // 🎨 DRAW BOARD
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
            ) {

                Image(
                    painter = painterResource(id = imageRes),
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
                                    currentPath?.let { path ->
                                        paths = paths + PathData(
                                            path,
                                            if (isEraser) Color.White else selectedColor,
                                            strokeWidth
                                        )
                                        currentPath = null

                                        try {
                                            if (!clickSound.isPlaying) clickSound.start()
                                        } catch (_: Exception) {}
                                    }
                                }
                            )
                        }
                ) {
                    paths.forEach {
                        drawPath(
                            path = it.path,
                            color = it.color,
                            style = Stroke(it.strokeWidth)
                        )
                    }

                    currentPath?.let {
                        drawPath(
                            path = it,
                            color = if (isEraser) Color.White else selectedColor,
                            style = Stroke(strokeWidth)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🧰 TOOLS
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = {
                    isEraser = false
                    clickSound.start()
                }) {
                    Icon(Icons.Default.Create, "brush", tint = Color.Blue)
                }

                IconButton(onClick = {
                    isEraser = true
                    strokeWidth = 40f
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

            // 🎨 COLORS
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(color, CircleShape)
                            .border(
                                if (selectedColor == color) 3.dp else 0.dp,
                                Color.Black,
                                CircleShape
                            )
                            .clickable {
                                selectedColor = color
                                isEraser = false
                                clickSound.start()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🎉 FINISH BUTTON
            Button(onClick = {
                val win = MediaPlayer.create(context, R.raw.win)
                win.start()
            }) {
                Text("🎉 Finished Coloring")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { clickSound.release() }
    }
}

