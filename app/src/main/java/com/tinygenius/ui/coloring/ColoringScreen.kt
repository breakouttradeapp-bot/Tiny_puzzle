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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tinygenius.data.local.PreferencesManager
import com.tinygenius.data.repository.ContentRepository
import com.tinygenius.ui.theme.*
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    onPageSelected: (Int) -> Unit
) {

    val context = LocalContext.current
    val contentRepository = remember { ContentRepository() }
    val preferencesManager = remember { PreferencesManager(context) }

    var isPremium by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isPremium = preferencesManager.isPremiumPurchased.first()
    }

    val coloringPages = remember(isPremium) {
        contentRepository.getAvailableColoringPages(isPremium)
    }

    if (coloringPages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No coloring pages available")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Choose a Coloring Page", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundLight,
                            PrimaryPink.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(coloringPages) { page ->

                    ColoringPageCard(
                        name = page.name,
                        imageRes = page.imageRes,
                        isLocked = page.isPremium && !isPremium,
                        onClick = {
                            if (!page.isPremium || isPremium) {
                                onPageSelected(page.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ColoringPageCard(
    name: String,
    imageRes: Int,
    isLocked: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .alpha(if (isLocked) 0.3f else 1f),
                contentScale = ContentScale.Fit
            )

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TextPrimary.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = PrimaryPink,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(8.dp),
                    color = TextLight
                )
            }
        }
    }
}

