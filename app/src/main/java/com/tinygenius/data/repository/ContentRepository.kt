package com.tinygenius.data.repository

import com.tinygenius.data.model.ColoringCategory
import com.tinygenius.data.model.ColoringPage
import com.tinygenius.data.model.PuzzleLevel

/**
 * Repository for managing app content (coloring pages and puzzles)
 */
class ContentRepository {
    
    /**
     * Get all coloring pages
     * Note: Replace drawable resource IDs with your actual drawable names
     */
    fun getAllColoringPages(): List<ColoringPage> {
        return listOf(
            // Free pages (1-3)
            ColoringPage(
                id = 1,
                name = "Cat",
                imageRes = getDrawableId("coloring_1"),
                isPremium = false,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 2,
                name = "Dog",
                imageRes = getDrawableId("coloring_2"),
                isPremium = false,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 3,
                name = "Apple",
                imageRes = getDrawableId("coloring_3"),
                isPremium = false,
                category = ColoringCategory.FRUITS
            ),
            
            // Premium pages (4-10)
            ColoringPage(
                id = 4,
                name = "Elephant",
                imageRes = getDrawableId("coloring_4"),
                isPremium = true,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 5,
                name = "Banana",
                imageRes = getDrawableId("coloring_5"),
                isPremium = true,
                category = ColoringCategory.FRUITS
            ),
            ColoringPage(
                id = 6,
                name = "Lion",
                imageRes = getDrawableId("coloring_6"),
                isPremium = true,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 7,
                name = "Rabbit",
                imageRes = getDrawableId("coloring_7"),
                isPremium = true,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 8,
                name = "Orange",
                imageRes = getDrawableId("coloring_8"),
                isPremium = true,
                category = ColoringCategory.FRUITS
            ),
            ColoringPage(
                id = 9,
                name = "Bird",
                imageRes = getDrawableId("coloring_9"),
                isPremium = true,
                category = ColoringCategory.ANIMALS
            ),
            ColoringPage(
                id = 10,
                name = "Fish",
                imageRes = getDrawableId("coloring_10"),
                isPremium = true,
                category = ColoringCategory.ANIMALS
            )
        )
    }
    
    /**
     * Get available coloring pages based on premium status
     */
    fun getAvailableColoringPages(isPremium: Boolean): List<ColoringPage> {
        val allPages = getAllColoringPages()
        return if (isPremium) {
            allPages
        } else {
            allPages.filter { !it.isPremium }
        }
    }
    
    /**
     * Get all puzzle levels
     */
    fun getAllPuzzleLevels(): List<PuzzleLevel> {
        return listOf(
            // Free level (4 pieces)
            PuzzleLevel(
                id = 1,
                name = "Butterfly",
                imageRes = getDrawableId("puzzle_1"),
                pieceCount = 4,
                isPremium = false
            ),
            PuzzleLevel(
                id = 2,
                name = "Flower",
                imageRes = getDrawableId("puzzle_2"),
                pieceCount = 4,
                isPremium = false
            ),
            PuzzleLevel(
                id = 3,
                name = "Rainbow",
                imageRes = getDrawableId("puzzle_3"),
                pieceCount = 4,
                isPremium = false
            ),
            
            // Premium levels (6 pieces)
            PuzzleLevel(
                id = 4,
                name = "Dolphin",
                imageRes = getDrawableId("puzzle_4"),
                pieceCount = 6,
                isPremium = true
            ),
            PuzzleLevel(
                id = 5,
                name = "Castle",
                imageRes = getDrawableId("puzzle_5"),
                pieceCount = 6,
                isPremium = true
            ),
            PuzzleLevel(
                id = 6,
                name = "Rocket",
                imageRes = getDrawableId("puzzle_6"),
                pieceCount = 6,
                isPremium = true
            ),
            
            // Premium levels (9 pieces)
            PuzzleLevel(
                id = 7,
                name = "Dinosaur",
                imageRes = getDrawableId("puzzle_7"),
                pieceCount = 9,
                isPremium = true
            ),
            PuzzleLevel(
                id = 8,
                name = "Unicorn",
                imageRes = getDrawableId("puzzle_8"),
                pieceCount = 9,
                isPremium = true
            ),
            PuzzleLevel(
                id = 9,
                name = "Princess",
                imageRes = getDrawableId("puzzle_9"),
                pieceCount = 9,
                isPremium = true
            )
        )
    }
    
    /**
     * Get available puzzle levels based on premium status
     */
    fun getAvailablePuzzleLevels(isPremium: Boolean): List<PuzzleLevel> {
        val allLevels = getAllPuzzleLevels()
        return if (isPremium) {
            allLevels
        } else {
            allLevels.filter { !it.isPremium }
        }
    }
    
    /**
     * Helper function to get drawable resource ID
     * Returns a placeholder if resource not found
     */
    private fun getDrawableId(name: String): Int {
        // This will be resolved at runtime
        // You need to add actual drawable resources
        return try {
            val context = android.content.res.Resources.getSystem()
            context.getIdentifier(name, "drawable", "com.tinygenius")
        } catch (e: Exception) {
            android.R.drawable.ic_menu_gallery // Placeholder
        }
    }
}
