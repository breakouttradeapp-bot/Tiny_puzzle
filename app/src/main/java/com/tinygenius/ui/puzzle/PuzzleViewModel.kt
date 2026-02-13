package com.tinygenius.ui.puzzle

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinygenius.data.local.PreferencesManager
import com.tinygenius.data.model.PuzzlePiece
import com.tinygenius.data.model.PuzzleState
import com.tinygenius.utils.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for Puzzle Game
 */
class PuzzleViewModel(
    private val context: Context,
    private val levelId: Int,
    private val pieceCount: Int
) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    private val soundManager = SoundManager(context)
    
    private val gridSize = when (pieceCount) {
        4 -> 2 // 2x2
        6 -> 3 // 2x3
        9 -> 3 // 3x3
        else -> 2
    }
    
    private val gridRows = when (pieceCount) {
        4 -> 2
        6 -> 2
        9 -> 3
        else -> 2
    }
    
    private val gridCols = when (pieceCount) {
        4 -> 2
        6 -> 3
        9 -> 3
        else -> 2
    }
    
    private val _puzzleState = MutableStateFlow(createInitialPuzzleState())
    val puzzleState: StateFlow<PuzzleState> = _puzzleState.asStateFlow()
    
    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()
    
    private val _isSoundEnabled = MutableStateFlow(true)
    
    init {
        loadSoundPreference()
    }
    
    private fun loadSoundPreference() {
        viewModelScope.launch {
            preferencesManager.isSoundEnabled.collect { enabled ->
                _isSoundEnabled.value = enabled
                soundManager.setSoundEnabled(enabled)
            }
        }
    }
    
    private fun createInitialPuzzleState(): PuzzleState {
        val pieces = mutableListOf<PuzzlePiece>()
        var id = 0
        
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                pieces.add(
                    PuzzlePiece(
                        id = id++,
                        correctRow = row,
                        correctCol = col,
                        currentOffset = Offset.Zero,
                        isPlaced = false,
                        gridColumns = gridCols,
                        gridRows = gridRows
                    )
                )
            }
        }
        
        // Shuffle pieces
        val shuffledPieces = pieces.shuffled(Random(System.currentTimeMillis()))
        
        return PuzzleState(
            pieces = shuffledPieces,
            completedPieces = emptySet(),
            isComplete = false,
            moves = 0
        )
    }
    
    fun updatePiecePosition(pieceId: Int, offset: Offset) {
        val currentState = _puzzleState.value
        val updatedPieces = currentState.pieces.map { piece ->
            if (piece.id == pieceId) {
                piece.copy(currentOffset = offset)
            } else {
                piece
            }
        }
        
        _puzzleState.value = currentState.copy(pieces = updatedPieces)
    }
    
    fun onPieceDragEnd(pieceId: Int, offset: Offset, containerWidth: Float, containerHeight: Float) {
        val currentState = _puzzleState.value
        val piece = currentState.pieces.find { it.id == pieceId } ?: return
        
        // Calculate which grid position this offset corresponds to
        val normalizedX = offset.x / containerWidth
        val normalizedY = offset.y / containerHeight
        
        val targetCol = (normalizedX * gridCols).toInt().coerceIn(0, gridCols - 1)
        val targetRow = (normalizedY * gridRows).toInt().coerceIn(0, gridRows - 1)
        
        // Check if this is the correct position
        val isCorrectPosition = targetCol == piece.correctCol && targetRow == piece.correctRow
        
        // Snap to grid position
        val snappedX = (targetCol.toFloat() / gridCols) * containerWidth
        val snappedY = (targetRow.toFloat() / gridRows) * containerHeight
        val snappedOffset = Offset(snappedX, snappedY)
        
        val updatedPieces = currentState.pieces.map { p ->
            if (p.id == pieceId) {
                p.copy(
                    currentOffset = snappedOffset,
                    isPlaced = true
                )
            } else {
                p
            }
        }
        
        val completedPieces = if (isCorrectPosition) {
            currentState.completedPieces + pieceId
        } else {
            currentState.completedPieces
        }
        
        val isComplete = completedPieces.size == pieceCount
        
        _puzzleState.value = currentState.copy(
            pieces = updatedPieces,
            completedPieces = completedPieces,
            isComplete = isComplete,
            moves = currentState.moves + 1
        )
        
        // Play sound
        if (isCorrectPosition) {
            soundManager.playSuccessSound()
        } else {
            soundManager.playTapSound()
        }
        
        // Show celebration if complete
        if (isComplete) {
            _showCelebration.value = true
        }
    }
    
    fun resetPuzzle() {
        _puzzleState.value = createInitialPuzzleState()
        _showCelebration.value = false
        soundManager.playTapSound()
    }
    
    fun hideCelebration() {
        _showCelebration.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
