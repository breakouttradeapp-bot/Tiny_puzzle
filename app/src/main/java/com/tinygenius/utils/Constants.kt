package com.tinygenius.utils

/**
 * Application-wide constants
 */
object Constants {
    // Billing
    const val PREMIUM_PRODUCT_ID = "premium_unlock"
    
    // AdMob Ad Units (Replace with your actual Ad Unit IDs)
    // For testing, use: ca-app-pub-3940256099942544/6300978111
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    
    // Content Limits
    const val FREE_COLORING_PAGES = 3
    const val TOTAL_COLORING_PAGES = 10
    
    // Puzzle Levels
    const val FREE_PUZZLE_LEVELS = 1 // Only 4-piece
    const val TOTAL_PUZZLE_LEVELS = 3 // 4, 6, 9 pieces
    
    // UI
    const val CELEBRATION_DURATION_MS = 2000L
    const val SOUND_ENABLED_KEY = "sound_enabled"
    const val PREMIUM_PURCHASED_KEY = "premium_purchased"
    
    // Parent Gate
    const val PARENT_GATE_MIN = 5
    const val PARENT_GATE_MAX = 15
    
    // Colors for palette
    val COLOR_PALETTE = listOf(
        0xFFFF0000, // Red
        0xFFFF6B00, // Orange
        0xFFFFFF00, // Yellow
        0xFF00FF00, // Green
        0xFF00FFFF, // Cyan
        0xFF0000FF, // Blue
        0xFF8B00FF, // Purple
        0xFFFF00FF, // Magenta
        0xFFFFB6C1, // Pink
        0xFF8B4513, // Brown
        0xFF000000, // Black
        0xFFFFFFFF, // White
        0xFF808080, // Gray
        0xFFFFA500, // Orange Light
        0xFF90EE90, // Light Green
        0xFFADD8E6, // Light Blue
        0xFFDDA0DD, // Plum
        0xFFF0E68C, // Khaki
        0xFFFFDAB9, // Peach
        0xFF98FB98  // Pale Green
    )
}
