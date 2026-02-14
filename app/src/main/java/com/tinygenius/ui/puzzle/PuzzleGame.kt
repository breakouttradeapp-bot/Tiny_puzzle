package com.tinygenius.ui.puzzle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.tinygenius.R
import kotlin.math.abs

data class Piece(
    val id:Int,
    val correctX:Float,
    val correctY:Float,
    val bitmap: Bitmap,
    var currentX:Float,
    var currentY:Float,
    var fixed:Boolean=false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleGame(levelId:Int, onNavigateBack:()->Unit){

    val context = LocalContext.current
    val winSound = remember { MediaPlayer.create(context, R.raw.win) }

    val grid = when(levelId){
        1->2
        2->3
        else->4
    }

    val imageBitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.puzzle1
    )

    val pieceWidth = imageBitmap.width/grid
    val pieceHeight = imageBitmap.height/grid
    val pieces = remember {

        val list = mutableStateListOf<Piece>()

        var id=0
        for(r in 0 until grid){
            for(c in 0 until grid){

                val bmp = Bitmap.createBitmap(
                    imageBitmap,
                    c*pieceWidth,
                    r*pieceHeight,
                    pieceWidth,
                    pieceHeight
                )

                list.add(
                    Piece(
                        id=id++,
                        correctX = (c*pieceWidth).toFloat(),
                        correctY = (r*pieceHeight).toFloat(),
                        bitmap = bmp,
                        currentX = (50..600).random().toFloat(),
                        currentY = (200..1200).random().toFloat()
                    )
                )
            }
        }
        list
    }

    var completed by remember { mutableStateOf(false) }

    Scaffold(
        topBar={
            TopAppBar(title={Text("🧩 Puzzle Level $levelId")})
        }
    ){ padding->
        Box(
            modifier=Modifier
                .fillMaxSize()
                .background(Color(0xFFE1F5FE))
                .padding(padding)
        ){

            // board center
            Box(
                modifier=Modifier
                    .align(Alignment.Center)
                    .size((pieceWidth*grid).dp)
                    .border(3.dp, Color.Black)
            )

            pieces.forEach { piece->

                var offset by remember { mutableStateOf(Offset(piece.currentX,piece.currentY)) }

                Image(
                    bitmap = piece.bitmap.asImageBitmap(),
                    contentDescription=null,
                    modifier=Modifier
                        .size(pieceWidth.dp, pieceHeight.dp)
                        .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                        .pointerInput(piece.id){

                            detectDragGestures(
                                onDragEnd={

                                    if(!piece.fixed){

                                        if(abs(offset.x - piece.correctX)<80 &&
                                           abs(offset.y - piece.correctY)<80){

                                            offset = Offset(piece.correctX, piece.correctY)
                                            piece.fixed=true

                                            if(pieces.all{it.fixed}){
                                                completed=true
                                                winSound.start()
                                            }
                                        }
                                    }
                                }
                            ){ change, drag->
                                change.consume()
                                offset+=drag
                                piece.currentX=offset.x
                                piece.currentY=offset.y
                            }
                        }
                )
            }

            if(completed){
                Text(
                    "🎉 Level Completed!",
                    modifier=Modifier.align(Alignment.TopCenter),
                    style=MaterialTheme.typography.headlineLarge,
                    color=Color(0xFF2E7D32)
                )
            }
        }
    }
}

