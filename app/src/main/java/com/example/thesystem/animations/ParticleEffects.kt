package com.example.thesystem.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val id: Long = System.nanoTime(),
    val color: Color,
    val initialXOffset: Float,
    val initialYOffset: Float,
    val initialSize: Float,
    val lifetimeMillis: Long = (1000L..2000L).random()
)

@Composable
fun ParticleBurstOverlay(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    particleCount: Int = 50,
    colors: List<Color> = listOf(Color.Yellow, Color(0xFFFFD700), Color.White)
) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    val density = LocalDensity.current.density

    LaunchedEffect(isVisible) {
        if (isVisible) {
            particles = List(particleCount) {
                Particle(
                    color = colors.random(),
                    initialXOffset = (Random.nextFloat() * 2f - 1f) * 50 * density,
                    initialYOffset = (Random.nextFloat() * 2f - 1f) * 50 * density,
                    initialSize = (4..10).random().toFloat() * density
                )
            }
            // clear particles after some time
            delay(2500L)
            particles = emptyList()
        } else {
            particles = emptyList()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            ParticleComposable(particle = particle, isVisible = isVisible)
        }
    }
}

@Composable
private fun ParticleComposable(particle: Particle, isVisible: Boolean) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(isVisible, particle.id) {
        if (isVisible) {
            animationProgress.snapTo(0f)
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = particle.lifetimeMillis.toInt(),
                    easing = LinearOutSlowInEasing
                )
            )
        } else {
            animationProgress.snapTo(0f)
        }
    }

    val currentAlpha = (1f - animationProgress.value).coerceIn(0f, 1f)
    val currentScale = 1f + animationProgress.value * 3f // Expands
    val angle = remember { (Random.nextFloat() * 2 * Math.PI).toFloat() } // Random direction
    val distance = animationProgress.value * 200.dp.value // How far it travels

    val xOffset = particle.initialXOffset + cos(angle) * distance
    val yOffset = particle.initialYOffset + sin(angle) * distance

    if (animationProgress.value < 1f && animationProgress.value > 0f) { // Only draw if active
        Canvas(
            modifier = Modifier
                .fillMaxSize() // Canvas covers the whole area
                .offset(xOffset.dp, yOffset.dp)
        ) {
            drawStar( // Or drawCircle
                color = particle.color.copy(alpha = currentAlpha),
                radius = particle.initialSize * currentScale / 2f, // radius for star/circle
                center = center // Draw relative to the center of the canvas
            )
        }
    }
}

fun DrawScope.drawStar(color: Color, radius: Float, center: Offset) {
    val path = androidx.compose.ui.graphics.Path()
    val numPoints = 5
    val innerRadius = radius / 2.5f
    for (i in 0 until numPoints * 2) {
        val currentRadius = if (i % 2 == 0) radius else innerRadius
        val angle = (i * Math.PI / numPoints - Math.PI / 2).toFloat()
        val x = center.x + cos(angle) * currentRadius
        val y = center.y + sin(angle) * currentRadius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}