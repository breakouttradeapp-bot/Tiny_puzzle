package com.tinygenius.ui.coloring

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coloring") }
            )
        }
    ) {
        Text("Coloring working")
    }
}

