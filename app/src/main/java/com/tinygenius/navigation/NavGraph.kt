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

        // 🟢 Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToColoring = {
                    navController.navigate(Screen.Coloring.route)
                },
                onNavigateToPuzzle = {
                    navController.navigate("puzzle_direct")
                }
            )
        }

        // 🟢 Coloring selection
        composable(Screen.Coloring.route) {
            ColoringScreen(
                onNavigateBack = { navController.popBackStack() },
                onPageSelected = { pageId ->
                    navController.navigate(Screen.ColoringCanvas.createRoute(pageId))
                }
            )
        }

        // 🟢 Coloring canvas
        composable(
            route = Screen.ColoringCanvas.route,
            arguments = listOf(
                navArgument("pageId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pageId = backStackEntry.arguments?.getInt("pageId") ?: 1
            ColoringCanvas(
                pageId = pageId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 🔥 DIRECT PUZZLE OPEN (NO CRASH)
        composable("puzzle_direct") {
            PuzzleGame(
                levelId = 1,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

