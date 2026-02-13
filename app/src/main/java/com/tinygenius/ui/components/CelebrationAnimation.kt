package com.tinygenius.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Celebration animation with colorful confetti and stars
 */
@Composable
fun CelebrationAnimation(
    modifier: Modifier = Modifier,
    particleCount: Int = 50
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                vx = Random.nextFloat() * 4 - 2,
                vy = Random.nextFloat() * -8 - 2,
                color = Color(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                ),
                size = Random.nextFloat() * 20f + 10f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f,
                shape = if (Random.nextBoolean()) ParticleShape.CIRCLE else ParticleShape.STAR
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val newX = particle.x + particle.vx * animationProgress
            val newY = particle.y + particle.vy * animationProgress + 9.8f * animationProgress * animationProgress * 0.1f
            val newRotation = particle.rotation + particle.rotationSpeed * animationProgress * 100
            
            val x = newX * size.width
            val y = newY * size.height
            
            if (x in 0f..size.width && y in 0f..size.height) {
                when (particle.shape) {
                    ParticleShape.CIRCLE -> {
                        drawCircle(
                            color = particle.color,
                            radius = particle.size,
                            center = Offset(x, y)
                        )
                    }
                    ParticleShape.STAR -> {
                        drawStar(
                            color = particle.color,
                            center = Offset(x, y),
                            size = particle.size,
                            rotation = newRotation
                        )
                    }
                }
            }
        }
    }
}

private enum class ParticleShape {
    CIRCLE, STAR
}

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val shape: ParticleShape
)

private fun DrawScope.drawStar(
    color: Color,
    center: Offset,
    size: Float,
    rotation: Float,
    points: Int = 5
) {
    val outerRadius = size
    val innerRadius = size * 0.5f
    val angle = (360f / points).toRadians()
    val startAngle = rotation.toRadians() - (Math.PI / 2).toFloat()
    
    val path = androidx.compose.ui.graphics.Path()
    
    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val currentAngle = startAngle + angle * i / 2
        
        val x = center.x + radius * cos(currentAngle)
        val y = center.y + radius * sin(currentAngle)
        
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    
    path.close()
    
    drawPath(
        path = path,
        color = color
    )
}

private fun Float.toRadians(): Float = this * Math.PI.toFloat() / 180f
