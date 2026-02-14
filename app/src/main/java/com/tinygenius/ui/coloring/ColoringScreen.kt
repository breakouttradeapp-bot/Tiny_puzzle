package com.tinygenius.ui.coloring

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    onPageSelected: (Int) -> Unit
) {

    val images = listOf(
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_compass,
        android.R.drawable.ic_menu_agenda
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Coloring Game") })
        }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(12.dp)
        ) {

            items(images.indices.toList()) { index ->

                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable { onPageSelected(index) }
                ) {

                    Image(
                        painter = painterResource(id = images[index]),
                        contentDescription = "img",
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

