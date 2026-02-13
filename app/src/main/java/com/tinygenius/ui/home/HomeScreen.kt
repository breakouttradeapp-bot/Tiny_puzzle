package com.tinygenius.ui.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.viewinterop.AndroidView
import com.tinygenius.data.repository.BillingRepository
import com.tinygenius.ui.components.ParentGate
import com.tinygenius.ui.theme.*
import com.tinygenius.utils.Constants

/**
 * Home Screen - Main entry point of the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToColoring: () -> Unit,
    onNavigateToPuzzle: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current)
    )
) {
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val showParentGate by viewModel.showParentGate.collectAsState()
    val purchaseState by viewModel.purchaseState.collectAsState()
    val context = LocalContext.current
    
    // Handle purchase state
    LaunchedEffect(purchaseState) {
        when (purchaseState) {
            is BillingRepository.PurchaseState.Success -> {
                // Show success message
                viewModel.resetPurchaseState()
            }
            is BillingRepository.PurchaseState.Error -> {
                // Show error message
                viewModel.resetPurchaseState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tiny Genius",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            if (!isPremium) {
                BannerAdView()
            }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                // App logo/title section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundCard
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = PrimaryPink
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coloring & Puzzle",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Main menu buttons
                MenuButton(
                    text = "🎨 Coloring",
                    icon = Icons.Default.Palette,
                    color = PrimaryPink,
                    onClick = onNavigateToColoring
                )
                
                MenuButton(
                    text = "🧩 Puzzle",
                    icon = Icons.Default.Extension,
                    color = PrimaryBlue,
                    onClick = onNavigateToPuzzle
                )
                
                if (!isPremium) {
                    MenuButton(
                        text = "⭐ Unlock Premium",
                        icon = Icons.Default.Star,
                        color = PremiumGold,
                        onClick = { viewModel.showParentGateForPurchase() }
                    )
                }
                
                // Sound toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = if (isSoundEnabled) PrimaryGreen else TextSecondary
                            )
                            Text(
                                text = "Sound",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Switch(
                            checked = isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextLight,
                                checkedTrackColor = PrimaryGreen,
                                uncheckedThumbColor = TextLight,
                                uncheckedTrackColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
    
    // Parent Gate Dialog
    if (showParentGate) {
        ParentGate(
            onDismiss = { viewModel.hideParentGate() },
            onSuccess = { viewModel.launchPurchase(context as Activity) }
        )
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = TextLight
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BannerAdView() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(Constants.BANNER_AD_UNIT_ID)
                
                // Request non-personalized ads for kids
                val adRequest = AdRequest.Builder()
                    .setRequestAgent("android_kotlin")
                    .build()
                
                loadAd(adRequest)
            }
        }
    )
}

// ViewModel Factory
class HomeViewModelFactory(
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
