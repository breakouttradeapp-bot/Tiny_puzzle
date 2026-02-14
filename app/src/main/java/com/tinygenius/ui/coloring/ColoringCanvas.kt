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

    var selectedColor by remember { mutableStateOf(Color.Red) }

    Scaffold(
        topBar={
            TopAppBar(title={Text("🎨 Coloring")})
        }
    ){ padding->

        Column(
            modifier=Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8E1))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Spacer(modifier=Modifier.height(20.dp))

            // coloring image
            Image(
                painter = painterResource(
                    if(pageId==1) R.drawable.coloring1 else R.drawable.coloring2
                ),
                contentDescription=null,
                modifier=Modifier
                    .size(300.dp)
                    .background(selectedColor.copy(alpha=0.2f))
            )

            Spacer(modifier=Modifier.height(30.dp))

            Text("Choose Color", style=MaterialTheme.typography.titleLarge)

            Spacer(modifier=Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){

                val colors=listOf(
                    Color.Red, Color.Green, Color.Blue,
                    Color.Yellow, Color.Magenta, Color.Cyan
                )

                colors.forEach { color->

                    Box(
                        modifier=Modifier
                            .size(50.dp)
                            .background(color, CircleShape)
                            .clickable{
                                selectedColor=color
                                clickSound.start()
                            }
                    )
                }
            }
        }
    }
}

