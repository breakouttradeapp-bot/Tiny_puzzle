package com.tinygenius.data.model

import androidx.annotation.DrawableRes

/**
 * Represents a coloring page
 */
data class ColoringPage(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val isPremium: Boolean = false,
    val category: ColoringCategory = ColoringCategory.ANIMALS
)

enum class ColoringCategory {
    ANIMALS,
    FRUITS,
    NATURE,
    VEHICLES
}

/**
 * Represents the state of a coloring region
 */
data class ColoringRegion(
    val id: Int,
    val color: Long = 0xFFFFFFFF, // Default white
    val path: android.graphics.Path? = null
)

/**
 * Coloring history for undo functionality
 */
data class ColoringAction(
    val regionId: Int,
    val previousColor: Long,
    val newColor: Long,
    val timestamp: Long = System.currentTimeMillis()
)
