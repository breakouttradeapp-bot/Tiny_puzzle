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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tinygenius.R

// Data class to hold drawing paths
data class PathData(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(pageId: Int, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    
    // Sound effects
    val clickSound = remember { MediaPlayer.create(context, R.raw.click) }
    
    // Drawing state
    var selectedColor by remember { mutableStateOf(Color.Red) }
    var paths by remember { mutableStateOf(listOf<PathData>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var strokeWidth by remember { mutableStateOf(15f) }
    var isEraserMode by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    // Image resource
    val imageRes = when (pageId) {
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }
    
    // Color palette
    val colors = listOf(
        Color(0xFFE53935), // Red
        Color(0xFFD81B60), // Pink
        Color(0xFF8E24AA), // Purple
        Color(0xFF5E35B1), // Deep Purple
        Color(0xFF3949AB), // Indigo
        Color(0xFF1E88E5), // Blue
        Color(0xFF039BE5), // Light Blue
        Color(0xFF00ACC1), // Cyan
        Color(0xFF00897B), // Teal
        Color(0xFF43A047), // Green
        Color(0xFF7CB342), // Light Green
        Color(0xFFC0CA33), // Lime
        Color(0xFFFDD835), // Yellow
        Color(0xFFFFB300), // Amber
        Color(0xFFFB8C00), // Orange
        Color(0xFFF4511E), // Deep Orange
        Color(0xFF6D4C41), // Brown
        Color(0xFF757575), // Gray
        Color(0xFF546E7A), // Blue Gray
        Color.Black,
        Color.White
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🎨 Kids Coloring",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF3E0),
                            Color(0xFFFFE0B2)
                        )
                    )
                )
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // Drawing Canvas
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // Background coloring image
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Coloring template",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                // Drawing Canvas
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
                                            path = path,
                                            color = if (isEraserMode) Color.White else selectedColor,
                                            strokeWidth = strokeWidth,
                                            isEraser = isEraserMode
                                        )
                                        currentPath = null
                                        clickSound.start()
                                    }
                                }
                            )
                        }
                ) {
                    // Draw all saved paths
                    paths.forEach { pathData ->
                        drawPath(
                            path = pathData.path,
                            color = pathData.color,
                            style = Stroke(
                                width = pathData.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    
                    // Draw current path being drawn
                    currentPath?.let { path ->
                        drawPath(
                            path = path,
                            color = if (isEraserMode) Color.White else selectedColor,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tools Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Tool buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brush tool
                        IconButton(
                            onClick = {
                                isEraserMode = false
                                clickSound.start()
                            },
                            modifier = Modifier
                                .background(
                                    if (!isEraserMode) Color(0xFF6200EA).copy(alpha = 0.2f) 
                                    else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Brush",
                                tint = if (!isEraserMode) Color(0xFF6200EA) else Color.Gray
                            )
                        }
                        
                        // Eraser tool
                        IconButton(
                            onClick = {
                                isEraserMode = true
                                strokeWidth = 30f
                                clickSound.start()
                            },
                            modifier = Modifier
                                .background(
                                    if (isEraserMode) Color(0xFF6200EA).copy(alpha = 0.2f) 
                                    else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Eraser",
                                tint = if (isEraserMode) Color(0xFF6200EA) else Color.Gray
                            )
                        }
                        
                        // Undo
                        IconButton(
                            onClick = {
                                if (paths.isNotEmpty()) {
                                    paths = paths.dropLast(1)
                                    clickSound.start()
                                }
                            },
                            enabled = paths.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Undo",
                                tint = if (paths.isNotEmpty()) Color(0xFF6200EA) else Color.Gray
                            )
                        }
                        
                        // Clear all
                        IconButton(
                            onClick = {
                                paths = emptyList()
                                clickSound.start()
                            },
                            enabled = paths.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = if (paths.isNotEmpty()) Color(0xFFE53935) else Color.Gray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Brush size slider
                    if (!isEraserMode) {
                        Text(
                            "Brush Size: ${strokeWidth.toInt()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Slider(
                            value = strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 5f..40f,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF6200EA),
                                activeTrackColor = Color(0xFF6200EA)
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Color Palette
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        "Colors",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EA)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Color grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.take(7).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (selectedColor == color && !isEraserMode) 3.dp else 0.dp,
                                        color = Color(0xFF6200EA),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor = color
                                        isEraserMode = false
                                        clickSound.start()
                                    }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.drop(7).take(7).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (selectedColor == color && !isEraserMode) 3.dp else 0.dp,
                                        color = Color(0xFF6200EA),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor = color
                                        isEraserMode = false
                                        clickSound.start()
                                    }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.drop(14).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (selectedColor == color && !isEraserMode) 3.dp else 0.dp,
                                        color = Color(0xFF6200EA),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor = color
                                        isEraserMode = false
                                        clickSound.start()
                                    }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    
    // Cleanup sound
    DisposableEffect(Unit) {
        onDispose {
            clickSound.release()
        }
    }
}

