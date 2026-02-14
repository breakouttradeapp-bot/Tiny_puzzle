package com.tinygenius.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tinygenius.ui.coloring.ColoringScreen
import com.tinygenius.ui.home.HomeScreen
import com.tinygenius.ui.puzzle.PuzzleGame

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // 🟢 Home
        composable("home") {
            HomeScreen(
                onNavigateToColoring = {
                    navController.navigate("coloring")
                },
                onNavigateToPuzzle = {
                    navController.navigate("puzzle")
                }
            )
        }

        // 🟢 Coloring simple
        composable("coloring") {
            ColoringScreen(
                onNavigateBack = { navController.popBackStack() },
                onPageSelected = { id: Int -> }
            )
        }

        // 🟢 Puzzle
        composable("puzzle") {
            PuzzleGame(
                levelId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

