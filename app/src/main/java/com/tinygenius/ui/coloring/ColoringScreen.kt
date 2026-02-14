package com.tinygenius.ui.coloring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tinygenius.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    onPageSelected: (Int) -> Unit
) {

    var selectedColor by remember { mutableStateOf(Color.White) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Coloring Game") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(selectedColor)
        ) {

            // image
            Image(
                painter = painterResource(id = R.drawable.coloring1),
                contentDescription = "coloring",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // color palette
            Row(modifier = Modifier.padding(16.dp)) {

                listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta).forEach { color ->

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(color)
                            .clickable { selectedColor = color }
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

