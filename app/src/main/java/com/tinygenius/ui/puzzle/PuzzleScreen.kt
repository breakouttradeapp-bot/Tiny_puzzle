package com.tinygenius.ui.puzzle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

/**
 * Puzzle level selection screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    onNavigateBack: () -> Unit,
    onLevelSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val contentRepository = remember { ContentRepository() }
    val preferencesManager = remember { PreferencesManager(context) }
    
    var isPremium by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isPremium = preferencesManager.isPremiumPurchased.first()
    }
    
    val puzzleLevels = remember(isPremium) {
        contentRepository.getAvailablePuzzleLevels(isPremium)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose a Puzzle",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
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
                            PrimaryBlue.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group by piece count
                val groupedLevels = puzzleLevels.groupBy { it.pieceCount }
                
                groupedLevels.forEach { (pieceCount, levels) ->
                    item {
                        Text(
                            text = "$pieceCount Piece Puzzles",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(levels) { level ->
                        PuzzleLevelCard(
                            name = level.name,
                            imageRes = level.imageRes,
                            pieceCount = level.pieceCount,
                            isLocked = level.isPremium && !isPremium,
                            onClick = {
                                if (!level.isPremium || isPremium) {
                                    onLevelSelected(level.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleLevelCard(
    name: String,
    imageRes: Int,
    pieceCount: Int,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image preview
            Card(
                modifier = Modifier.size(88.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = name,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isLocked) 0.3f else 1f),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(TextPrimary.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                modifier = Modifier.size(32.dp),
                                tint = PremiumGold
                            )
                        }
                    }
                }
            }
            
            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) TextSecondary else TextPrimary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (pieceCount) {
                            4 -> PrimaryGreen.copy(alpha = 0.2f)
                            6 -> PrimaryOrange.copy(alpha = 0.2f)
                            9 -> AccentError.copy(alpha = 0.2f)
                            else -> TextSecondary.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = "$pieceCount pieces",
                            style = MaterialTheme.typography.labelLarge,
                            color = when (pieceCount) {
                                4 -> PrimaryGreen
                                6 -> PrimaryOrange
                                9 -> AccentError
                                else -> TextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (pieceCount) {
                            4 -> PrimaryGreen.copy(alpha = 0.2f)
                            6 -> PrimaryOrange.copy(alpha = 0.2f)
                            9 -> AccentError.copy(alpha = 0.2f)
                            else -> TextSecondary.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = when (pieceCount) {
                                4 -> "Easy"
                                6 -> "Medium"
                                9 -> "Hard"
                                else -> "Unknown"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = when (pieceCount) {
                                4 -> PrimaryGreen
                                6 -> PrimaryOrange
                                9 -> AccentError
                                else -> TextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
