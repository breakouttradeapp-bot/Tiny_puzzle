package com.tinygenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.tinygenius.navigation.NavGraph
import androidx.compose.animation.core.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var showSplash by remember { mutableStateOf(true) }

            // 🔥 animation scale
            val scale = remember { Animatable(0.5f) }

            if (showSplash) {

                LaunchedEffect(true) {
                    scale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(1200)
                    )
                    delay(2000)
                    showSplash = false
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier
                            .size(220.dp)
                            .scale(scale.value)
                    )
                }

            } else {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}

