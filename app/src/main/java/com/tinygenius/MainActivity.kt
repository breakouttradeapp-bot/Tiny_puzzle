package com.tinygenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.tinygenius.navigation.NavGraph
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier.size(220.dp)
                    )
                }

                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }

            } else {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}

