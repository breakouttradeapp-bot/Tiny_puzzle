package com.tinygenius.ui.coloring

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.tinygenius.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringCanvas(pageId:Int, onNavigateBack:()->Unit){

    val context = LocalContext.current
    val clickSound = remember { MediaPlayer.create(context, R.raw.click) }

    var currentColor by remember { mutableStateOf(Color.Red) }
    var bgColor by remember { mutableStateOf(Color.White) }

    val imageRes = when(pageId){
        1 -> R.drawable.coloring1
        else -> R.drawable.coloring2
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🎨 Coloring Fun") })
        }
    ){ padding->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF3E0))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Spacer(modifier = Modifier.height(20.dp))

            // Coloring board
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .background(bgColor)
                    .clickable {
                        bgColor = currentColor
                        clickSound.start()
                    },
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text("Pick a Color", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){

                val colors = listOf(
                    Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Magenta, Color.Cyan,
                    Color(0xFFFF9800), Color(0xFF795548)
                )

                colors.forEach { color->

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color, CircleShape)
                            .clickable {
                                currentColor = color
                                clickSound.start()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(onClick = {
                bgColor = Color.White
            }) {
                Text("Reset")
            }
        }
    }
}

