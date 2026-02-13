package com.tinygenius.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset

/**
 * Represents a puzzle level
 */
data class PuzzleLevel(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val pieceCount: Int, // 4, 6, or 9
    val isPremium: Boolean = false
)

/**
 * Represents a single puzzle piece
 */
data class PuzzlePiece(
    val id: Int,
    val correctRow: Int,
    val correctCol: Int,
    val currentOffset: Offset = Offset.Zero,
    val isPlaced: Boolean = false,
    val gridColumns: Int,
    val gridRows: Int
) {
    fun isInCorrectPosition(tolerance: Float = 50f): Boolean {
        if (!isPlaced) return false
        
        // Calculate expected position based on correct row/col
        val expectedX = correctCol * (1f / gridColumns)
        val expectedY = correctRow * (1f / gridRows)
        
        // Check if current offset is within tolerance
        val currentX = currentOffset.x
        val currentY = currentOffset.y
        
        val dx = Math.abs(currentX - expectedX)
        val dy = Math.abs(currentY - expectedY)
        
        return dx < tolerance && dy < tolerance
    }
}

/**
 * State of the entire puzzle
 */
data class PuzzleState(
    val pieces: List<PuzzlePiece>,
    val completedPieces: Set<Int> = emptySet(),
    val isComplete: Boolean = false,
    val moves: Int = 0
)
