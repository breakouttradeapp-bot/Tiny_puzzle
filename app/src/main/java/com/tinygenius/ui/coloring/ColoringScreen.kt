package com.tinygenius.ui.coloring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tinygenius.data.repository.ContentRepository
import com.tinygenius.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    onPageSelected: (Int) -> Unit
) {

    val contentRepository = remember { ContentRepository() }

    // 🔥 NO premium check (avoid crash)
    val coloringPages = remember {
        contentRepository.getAvailableColoringPages(true)
    }

    if (coloringPages.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No coloring pages found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coloring Pages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(BackgroundLight, PrimaryPink.copy(alpha = 0.1f))
                    )
                )
                .padding(padding)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(coloringPages) { page ->

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onPageSelected(page.id) },
                        shape = RoundedCornerShape(20.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                painter = painterResource(id = page.imageRes),
                                contentDescription = page.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )

                            Text(
                                page.name,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

