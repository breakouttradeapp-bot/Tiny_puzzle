package com.tinygenius.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tinygenius.ui.coloring.ColoringCanvas
import com.tinygenius.ui.coloring.ColoringScreen
import com.tinygenius.ui.home.HomeScreen
import com.tinygenius.ui.puzzle.PuzzleGame

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToColoring = {
                    navController.navigate(Screen.Coloring.route)
                },
                onNavigateToPuzzle = {
                    navController.navigate(Screen.Puzzle.route)
                }
            )
        }

        // Coloring Selection Screen
        composable(Screen.Coloring.route) {
            ColoringScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPageSelected = { pageId ->
                    navController.navigate(Screen.ColoringCanvas.createRoute(pageId))
                }
            )
        }

        // Coloring Canvas Screen
        composable(
            route = Screen.ColoringCanvas.route,
            arguments = listOf(
                navArgument("pageId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pageId = backStackEntry.arguments?.getInt("pageId") ?: 1
            ColoringCanvas(
                pageId = pageId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 🔥 DIRECT PUZZLE OPEN (CRASH FIX)
        composable(Screen.Puzzle.route) {
            PuzzleGame(
                levelId = 1,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Puzzle Game with level id
        composable(
            route = Screen.PuzzleGame.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            PuzzleGame(
                levelId = levelId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

