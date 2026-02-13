package com.tinygenius.ui.puzzle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tinygenius.data.repository.ContentRepository
import com.tinygenius.ui.components.CelebrationAnimation
import com.tinygenius.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(
    levelId: Int,
    onNavigateBack: () -> Unit,
    viewModel: PuzzleViewModel = viewModel(
        factory = PuzzleViewModelFactory(
            LocalContext.current,
            levelId,
            getPieceCountForLevel(levelId)
        )
    )
) {
    val puzzleState by viewModel.puzzleState.collectAsState()
    val showCelebration by viewModel.showCelebration.collectAsState()

    val context = LocalContext.current
    val contentRepository = remember { ContentRepository() }
    val level = remember { contentRepository.getAllPuzzleLevels().find { it.id == levelId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(level?.name ?: "Puzzle", fontWeight = FontWeight.Bold)
                        Text("Moves: ${puzzleState.moves}", color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetPuzzle() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = PrimaryBlue)
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
        ) {
            level?.let {
                PuzzleBoard(
                    imageRes = it.imageRes,
                    puzzleState = puzzleState,
                    onPieceDragged = { pieceId, offset ->
                        viewModel.updatePiecePosition(pieceId, offset)
                    },
                    onPieceDragEnd = { pieceId, offset, width, height ->
                        viewModel.onPieceDragEnd(pieceId, offset, width, height)
                    }
                )
            }

            if (showCelebration) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CelebrationAnimation()
                }
            }
        }
    }
}

@Composable
private fun PuzzleBoard(
    imageRes: Int,
    puzzleState: com.tinygenius.data.model.PuzzleState,
    onPieceDragged: (Int, Offset) -> Unit,
    onPieceDragEnd: (Int, Offset, Float, Float) -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(imageRes) {
        try {
            context.resources.getDrawable(imageRes, null)?.let { drawable ->
                val bmp = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        } catch (e: Exception) { null }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        val containerWidth = constraints.maxWidth.toFloat()
        val containerHeight = constraints.maxHeight.toFloat()

        val gridCols = puzzleState.pieces.firstOrNull()?.gridColumns ?: 2
        val gridRows = puzzleState.pieces.firstOrNull()?.gridRows ?: 2

        val pieceWidth = containerWidth / gridCols
        val pieceHeight = containerHeight / gridRows

        bitmap?.let { bmp ->
            puzzleState.pieces.forEach { piece ->
                PuzzlePieceView(
                    piece = piece,
                    bitmap = bmp,
                    pieceWidth = pieceWidth,
                    pieceHeight = pieceHeight,
                    containerWidth = containerWidth,
                    containerHeight = containerHeight,
                    isCompleted = puzzleState.completedPieces.contains(piece.id),
                    isGameComplete = puzzleState.isComplete,   // ⭐ FIX ADDED
                    onDragged = { offset ->
                        onPieceDragged(piece.id, offset)
                    },
                    onDragEnd = { offset ->
                        onPieceDragEnd(piece.id, offset, containerWidth, containerHeight)
                    }
                )
            }
        }
    }
}

@Composable
private fun PuzzlePieceView(
    piece: com.tinygenius.data.model.PuzzlePiece,
    bitmap: Bitmap,
    pieceWidth: Float,
    pieceHeight: Float,
    containerWidth: Float,
    containerHeight: Float,
    isCompleted: Boolean,
    isGameComplete: Boolean,   // ⭐ NEW PARAMETER
    onDragged: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
) {
    var offsetX by remember(piece.id) { mutableStateOf(piece.currentOffset.x) }
    var offsetY by remember(piece.id) { mutableStateOf(piece.currentOffset.y) }

    LaunchedEffect(piece.currentOffset) {
        offsetX = piece.currentOffset.x
        offsetY = piece.currentOffset.y
    }

    val bitmapPieceWidth = bitmap.width / piece.gridColumns
    val bitmapPieceHeight = bitmap.height / piece.gridRows

    val pieceBitmap = remember(bitmap, piece.id) {
        try {
            Bitmap.createBitmap(
                bitmap,
                piece.correctCol * bitmapPieceWidth,
                piece.correctRow * bitmapPieceHeight,
                bitmapPieceWidth,
                bitmapPieceHeight
            )
        } catch (e: Exception) { null }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(
                width = with(LocalDensity.current) { pieceWidth.toDp() },
                height = with(LocalDensity.current) { pieceHeight.toDp() }
            )
            .pointerInput(piece.id) {
                if (!isGameComplete) {   // ⭐ FIX HERE
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newOffsetX = (offsetX + dragAmount.x)
                                .coerceIn(0f, containerWidth - pieceWidth)
                            val newOffsetY = (offsetY + dragAmount.y)
                                .coerceIn(0f, containerHeight - pieceHeight)

                            offsetX = newOffsetX
                            offsetY = newOffsetY
                            onDragged(Offset(newOffsetX, newOffsetY))
                        },
                        onDragEnd = {
                            onDragEnd(Offset(offsetX, offsetY))
                        }
                    )
                }
            }
    ) {
        pieceBitmap?.let { pieceBmp ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawBitmap(
                        pieceBmp,
                        null,
                        android.graphics.RectF(0f, 0f, size.width, size.height),
                        null
                    )

                    drawRect(
                        color = if (isCompleted) Color.Green else Color.White,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        style = Stroke(width = if (isCompleted) 6f else 2f)
                    )
                }
            }
        }
    }
}

private fun getPieceCountForLevel(levelId: Int): Int {
    return when (levelId) {
        in 1..3 -> 4
        in 4..6 -> 6
        in 7..9 -> 9
        else -> 4
    }
}

class PuzzleViewModelFactory(
    private val context: android.content.Context,
    private val levelId: Int,
    private val pieceCount: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuzzleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PuzzleViewModel(context, levelId, pieceCount) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

