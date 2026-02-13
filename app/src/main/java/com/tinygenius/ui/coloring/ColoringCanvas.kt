package com.tinygenius.ui.coloring

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinygenius.data.repository.ContentRepository
import com.tinygenius.ui.components.CelebrationAnimation
import com.tinygenius.ui.theme.*
import com.tinygenius.utils.Constants
import java.util.LinkedList
import java.util.Queue

/**
 * Coloring Canvas Screen with tap-to-fill functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(
    pageId: Int,
    onNavigateBack: () -> Unit,
    viewModel: ColoringViewModel = viewModel(
        factory = ColoringViewModelFactory(LocalContext.current, pageId)
    )
) {
    val selectedColor by viewModel.selectedColor.collectAsState()
    val coloredRegions by viewModel.coloredRegions.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()
    val canUndo by remember { derivedStateOf { viewModel.undoStack.value.isNotEmpty() } }
    
    val context = LocalContext.current
    val contentRepository = remember { ContentRepository() }
    val page = remember { contentRepository.getAllColoringPages().find { it.id == pageId } }
    
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = page?.name ?: "Coloring",
                        style = MaterialTheme.typography.titleLarge,
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
                actions = {
                    // Undo button
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) PrimaryBlue else TextSecondary.copy(alpha = 0.4f)
                        )
                    }
                    
                    // Clear button
                    IconButton(onClick = { viewModel.clear() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = AccentError
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            ColorPalette(
                selectedColor = selectedColor,
                onColorSelected = { viewModel.selectColor(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
        ) {
            // Coloring Image
            page?.let {
                ColoringImage(
                    imageRes = it.imageRes,
                    coloredRegions = coloredRegions,
                    onRegionTapped = { x, y, imageBitmap ->
                        // Simple region detection based on tap position
                        // In production, implement flood-fill algorithm
                        val regionId = calculateRegionId(x, y, imageBitmap.width, imageBitmap.height)
                        viewModel.colorRegion(regionId)
                    },
                    selectedColor = selectedColor
                )
            }
            
            // Celebration overlay
            if (showCelebration) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .pointerInput(Unit) {
                            detectTapGestures {
                                viewModel.hideCelebration()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CelebrationAnimation()
                    
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundCard
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = PremiumGold
                            )
                            Text(
                                text = "Amazing!",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryPink
                            )
                            Text(
                                text = "You colored the picture!",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColoringImage(
    imageRes: Int,
    coloredRegions: Map<Int, Long>,
    onRegionTapped: (Float, Float, Bitmap) -> Unit,
    selectedColor: Long
) {
    val context = LocalContext.current
    val bitmap = remember(imageRes) {
        try {
            context.resources.getDrawable(imageRes, null)?.let { drawable ->
                val bmp = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        } catch (e: Exception) {
            null
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let { bmp ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            onRegionTapped(offset.x, offset.y, bmp)
                        }
                    }
            ) {
                // Draw the bitmap
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawBitmap(bmp, 0f, 0f, null)
                }
                
                // Draw colored regions (simplified overlay)
                coloredRegions.forEach { (regionId, color) ->
                    // This is a simplified version
                    // In production, implement proper region coloring
                    val alpha = 0.6f
                    drawCircle(
                        color = Color(color).copy(alpha = alpha),
                        radius = 30f,
                        center = Offset(
                            x = (regionId % 20) * (size.width / 20),
                            y = (regionId / 20) * (size.height / 20)
                        )
                    )
                }
            }
        } ?: run {
            // Fallback if bitmap loading fails
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Coloring image",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Use placeholder bitmap
                            val placeholderBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
                            onRegionTapped(offset.x, offset.y, placeholderBitmap)
                        }
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ColorPalette(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Pick a Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Constants.COLOR_PALETTE) { color ->
                    ColorButton(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorButton(
    color: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 56.dp else 48.dp)
            .clip(CircleShape)
            .background(Color(color))
            .pointerInput(Unit) {
                detectTapGestures { onClick() }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == 0xFFFFFFFF || color == 0xFFF0E68C) Color.Black else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Calculate region ID based on tap position
 * This is a simplified version - in production, use flood fill algorithm
 */
private fun calculateRegionId(x: Float, y: Float, width: Int, height: Int): Int {
    val gridSize = 20
    val col = ((x / width) * gridSize).toInt().coerceIn(0, gridSize - 1)
    val row = ((y / height) * gridSize).toInt().coerceIn(0, gridSize - 1)
    return row * gridSize + col
}

// ViewModel Factory
class ColoringViewModelFactory(
    private val context: android.content.Context,
    private val pageId: Int
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ColoringViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ColoringViewModel(context, pageId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
