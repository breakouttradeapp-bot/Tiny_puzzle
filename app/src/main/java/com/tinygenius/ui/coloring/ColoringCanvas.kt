package com.tinygenius.ui.coloring

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.tinygenius.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(pageId: Int, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Sound effects
    val clickSound = remember { MediaPlayer.create(context, R.raw.click) }
    val fillSound = remember { MediaPlayer.create(context, R.raw.click) }
    
    // State
    var selectedColor by remember { mutableStateOf(Color(0xFFE53935)) }
    var coloringBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var bitmapKey by remember { mutableStateOf(0) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var showCelebration by remember { mutableStateOf(false) }
    var filledRegions by remember { mutableStateOf(0) }
    
    // Image resource
    val imageRes = when (pageId) {
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }
    
    // Initialize bitmap
    LaunchedEffect(imageRes, canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            withContext(Dispatchers.IO) {
                val originalBitmap = android.graphics.BitmapFactory.decodeResource(
                    context.resources,
                    imageRes
                )
                coloringBitmap = Bitmap.createScaledBitmap(
                    originalBitmap,
                    canvasSize.width,
                    canvasSize.height,
                    true
                ).copy(Bitmap.Config.ARGB_8888, true)
            }
        }
    }
    
    // Flood fill algorithm
    fun floodFill(bitmap: Bitmap, x: Int, y: Int, targetColor: Int, replacementColor: Int) {
        if (x < 0 || x >= bitmap.width || y < 0 || y >= bitmap.height) return
        if (targetColor == replacementColor) return
        
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(Pair(x, y))
        
        val visited = mutableSetOf<Pair<Int, Int>>()
        
        while (queue.isNotEmpty()) {
            val (currentX, currentY) = queue.poll() ?: continue
            
            if (currentX < 0 || currentX >= bitmap.width || 
                currentY < 0 || currentY >= bitmap.height) continue
            
            if (visited.contains(Pair(currentX, currentY))) continue
            visited.add(Pair(currentX, currentY))
            
            val index = currentY * bitmap.width + currentX
            val currentColor = pixels[index]
            
            // Check if colors are similar (tolerance for anti-aliasing)
            if (isSimilarColor(currentColor, targetColor, tolerance = 40)) {
                pixels[index] = replacementColor
                
                queue.add(Pair(currentX + 1, currentY))
                queue.add(Pair(currentX - 1, currentY))
                queue.add(Pair(currentX, currentY + 1))
                queue.add(Pair(currentX, currentY - 1))
            }
        }
        
        bitmap.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    }
    
    fun isSimilarColor(color1: Int, color2: Int, tolerance: Int): Boolean {
        val r1 = android.graphics.Color.red(color1)
        val g1 = android.graphics.Color.green(color1)
        val b1 = android.graphics.Color.blue(color1)
        
        val r2 = android.graphics.Color.red(color2)
        val g2 = android.graphics.Color.green(color2)
        val b2 = android.graphics.Color.blue(color2)
        
        return Math.abs(r1 - r2) <= tolerance &&
               Math.abs(g1 - g2) <= tolerance &&
               Math.abs(b1 - b2) <= tolerance
    }
    
    // Color palette with vibrant colors
    val colorPalette = listOf(
        // Row 1 - Reds & Pinks
        Color(0xFFE53935), Color(0xFFD81B60), Color(0xFFAD1457), 
        Color(0xFFFF6B9D), Color(0xFFFF4081), Color(0xFFF06292),
        
        // Row 2 - Purples & Blues
        Color(0xFF8E24AA), Color(0xFF5E35B1), Color(0xFF3949AB),
        Color(0xFF1E88E5), Color(0xFF039BE5), Color(0xFF00ACC1),
        
        // Row 3 - Greens & Yellows
        Color(0xFF00897B), Color(0xFF43A047), Color(0xFF7CB342),
        Color(0xFFC0CA33), Color(0xFFFDD835), Color(0xFFFFB300),
        
        // Row 4 - Oranges & Browns
        Color(0xFFFB8C00), Color(0xFFF4511E), Color(0xFF6D4C41),
        Color(0xFF8D6E63), Color(0xFF757575), Color(0xFF424242)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "🎨 Color Fill Fun",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Tap to fill regions!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF9C4),
                            Color(0xFFFFE082),
                            Color(0xFFFFD54F)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Coloring Canvas
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coordinates ->
                                canvasSize = coordinates.size
                            }
                            .pointerInput(coloringBitmap, selectedColor) {
                                detectTapGestures { offset ->
                                    coloringBitmap?.let { bitmap ->
                                        val x = (offset.x * bitmap.width / size.width).toInt()
                                        val y = (offset.y * bitmap.height / size.height).toInt()
                                        
                                        if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                                            scope.launch(Dispatchers.Default) {
                                                val targetColor = bitmap.getPixel(x, y)
                                                val replacementColor = selectedColor.toArgb()
                                                
                                                floodFill(bitmap, x, y, targetColor, replacementColor)
                                                
                                                withContext(Dispatchers.Main) {
                                                    bitmapKey++
                                                    filledRegions++
                                                    fillSound.start()
                                                    
                                                    // Show celebration at milestones
                                                    if (filledRegions % 5 == 0) {
                                                        showCelebration = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        coloringBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Coloring canvas",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                key = bitmapKey
                            )
                        }
                        
                        // Celebration overlay
                        if (showCelebration) {
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(1500)
                                showCelebration = false
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🎉 Great Job! 🎉",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tools Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset button
                        Button(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    val originalBitmap = android.graphics.BitmapFactory.decodeResource(
                                        context.resources,
                                        imageRes
                                    )
                                    coloringBitmap = Bitmap.createScaledBitmap(
                                        originalBitmap,
                                        canvasSize.width,
                                        canvasSize.height,
                                        true
                                    ).copy(Bitmap.Config.ARGB_8888, true)
                                    
                                    withContext(Dispatchers.Main) {
                                        bitmapKey++
                                        filledRegions = 0
                                        clickSound.start()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset")
                        }
                        
                        // Stats
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF6200EA).copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFDD835),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Regions Filled",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        filledRegions.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6200EA)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Color Palette Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "🎨 Pick Your Color",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EA)
                            )
                            
                            // Selected color indicator
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(selectedColor, CircleShape)
                                    .border(3.dp, Color(0xFF6200EA), CircleShape)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Color rows
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (row in 0..3) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    colorPalette.drop(row * 6).take(6).forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .shadow(4.dp, CircleShape)
                                                .background(color, CircleShape)
                                                .border(
                                                    width = if (selectedColor == color) 4.dp else 0.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    selectedColor = color
                                                    clickSound.start()
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            clickSound.release()
            fillSound.release()
        }
    }
}

