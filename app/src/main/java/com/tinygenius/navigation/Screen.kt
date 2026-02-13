package com.tinygenius.navigation

/**
 * Sealed class representing app screens for navigation
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Coloring : Screen("coloring")
    object ColoringCanvas : Screen("coloring_canvas/{pageId}") {
        fun createRoute(pageId: Int) = "coloring_canvas/$pageId"
    }
    object Puzzle : Screen("puzzle")
    object PuzzleGame : Screen("puzzle_game/{levelId}") {
        fun createRoute(levelId: Int) = "puzzle_game/$levelId"
    }
}
