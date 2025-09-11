package com.example.thesystem.animations

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

enum class EdgeLightState {
    Idle, Pulsing
}

@Composable
fun FullScreenEdgeLightingEffect(
    edgeLightState: EdgeLightState,
    pulseColor: Color = MaterialTheme.colorScheme.primary,
    pulseWidth: Dp = 6.dp,
    animationDurationMillis: Int = 1500
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current

    val pulseWidthPx = with(density) { pulseWidth.toPx() }

    // Animation for the pulse effect (e.g., alpha or sweep position)
    val infiniteTransition = rememberInfiniteTransition(label = "edgeLightPulse")
    val pulseAnimationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "edgeLightPulseValue"
    )

    // More controlled animation for when the state changes to Pulsing
    val overallAlpha = remember { Animatable(0f) }
    LaunchedEffect(edgeLightState) {
        if (edgeLightState == EdgeLightState.Pulsing) {
            overallAlpha.animateTo(1f, animationSpec = tween(500))
        } else {
            overallAlpha.animateTo(0f, animationSpec = tween(500))
        }
    }

    if (overallAlpha.value > 0f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate the current position for the sweeping light effect
            // This example makes two lights sweep from center to edges and fade
            val sweepPosition = canvasHeight * pulseAnimationValue
            val gradientMidPoint = 0.5f // Where the solid color is, fades out from here

            val topEdgeColors = listOf(
                Color.Transparent,
                pulseColor.copy(alpha = 0.7f * overallAlpha.value),
                Color.Transparent
            )
            val bottomEdgeColors = topEdgeColors.asReversed()


            // --- Example: Two lights sweeping vertically ---

            // Light 1: Sweeps from top-center downwards
            val light1StartY = 0f
            val light1EndY =
                canvasHeight * pulseAnimationValue * 2 - canvasHeight * 0.5f // Moves across screen
            val light1Gradient = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    pulseColor.copy(alpha = 0.8f * overallAlpha.value),
                    Color.Transparent
                ),
                startY = (light1EndY - pulseWidthPx * 4).coerceAtLeast(0f),
                endY = (light1EndY + pulseWidthPx * 4).coerceAtMost(canvasHeight)
            )
            if (light1EndY > -pulseWidthPx * 4 && light1EndY < canvasHeight + pulseWidthPx * 4) {
                // Left Edge for light 1
                drawLine(
                    brush = light1Gradient,
                    start = Offset(pulseWidthPx / 2, 0f),
                    end = Offset(pulseWidthPx / 2, canvasHeight),
                    strokeWidth = pulseWidthPx,
                    cap = StrokeCap.Round
                )
                // Right Edge for light 1
                drawLine(
                    brush = light1Gradient,
                    start = Offset(canvasWidth - pulseWidthPx / 2, 0f),
                    end = Offset(canvasWidth - pulseWidthPx / 2, canvasHeight),
                    strokeWidth = pulseWidthPx,
                    cap = StrokeCap.Round
                )
            }


            // Light 2: Sweeps from bottom-center upwards (opposite direction)
            val light2StartY = canvasHeight
            val light2EndY =
                canvasHeight - (canvasHeight * pulseAnimationValue * 2 - canvasHeight * 0.5f)
            val light2Gradient = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    pulseColor.copy(alpha = 0.8f * overallAlpha.value),
                    Color.Transparent
                ),
                startY = (light2EndY - pulseWidthPx * 4).coerceAtLeast(0f),
                endY = (light2EndY + pulseWidthPx * 4).coerceAtMost(canvasHeight)

            )

            if (light2EndY > -pulseWidthPx * 4 && light2EndY < canvasHeight + pulseWidthPx * 4) {
                // Top Edge for light 2
                drawLine(
                    brush = light2Gradient, // Horizontal gradient for top/bottom edges
                    start = Offset(0f, pulseWidthPx / 2),
                    end = Offset(canvasWidth, pulseWidthPx / 2),
                    strokeWidth = pulseWidthPx,
                    cap = StrokeCap.Round
                )
                // Bottom Edge for light 2
                drawLine(
                    brush = light2Gradient,
                    start = Offset(0f, canvasHeight - pulseWidthPx / 2),
                    end = Offset(canvasWidth, canvasHeight - pulseWidthPx / 2),
                    strokeWidth = pulseWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

fun Modifier.animatedCardEdgeGlow(
    isVisible: Boolean,
    glowColor: Color = Color.Yellow,
    glowWidth: Dp = 8.dp,
    animationDurationMillis: Int = 1000,
    cornerRadius: Dp = 16.dp
): Modifier = composed {
    val alphaAnim = remember { Animatable(0f) }
    val glowWidthPx = with(LocalDensity.current) { glowWidth.toPx() }
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }

    LaunchedEffect(isVisible) {
        alphaAnim.animateTo(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(if (isVisible) animationDurationMillis / 2 else animationDurationMillis)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cardGlowPulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDurationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardGlowPulseValue"
    )

    this.drawBehind {
        if (alphaAnim.value > 0) {
            val paint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = glowWidthPx // The full width of the glow
                color = glowColor
                    .copy(alpha = (alphaAnim.value * pulseAnim).coerceIn(0f, 1f))
                    .toArgb()
                strokeJoin = android.graphics.Paint.Join.ROUND
                strokeCap = android.graphics.Paint.Cap.ROUND
                maskFilter = BlurMaskFilter(
                    glowWidthPx * 0.75f, // Blur radius is key for softness
                    BlurMaskFilter.Blur.NORMAL
                )
            }

            // We define the rectangle for the stroke.
            // The stroke is drawn centered on this path.
            // To make the glow appear to start right at the edge of the *original* composable,
            // the path for the stroke should be slightly inset if the glow is purely outside,
            // or exactly the composable's bounds if the glow should visually "bleed" from the edge.

            // For a glow that seems to emanate *from* the edge and expand outwards:
            // Define the rectangle slightly smaller than the component size,
            // so that the stroke + blur expands to cover the edge and beyond.

            // Let's draw the round rect for the stroke path slightly inset,
            // so the center of the stroke aligns with the component's edge.
            // The glowWidthPx / 2f handles this.
            val rectLeft = glowWidthPx / 2f
            val rectTop = glowWidthPx / 2f
            val rectRight = size.width - glowWidthPx / 2f
            val rectBottom = size.height - glowWidthPx / 2f

            // Ensure the rectangle is valid before drawing
            if (rectLeft < rectRight && rectTop < rectBottom) {
                // The corner radius for the path should be the card's corner radius
                // minus half the stroke width, to ensure the *outermost part* of the
                // blurred stroke has the desired visual corner radius.
                // This needs to be at least 0.
                val pathCornerRadius = (cornerRadiusPx - glowWidthPx / 2f).coerceAtLeast(0f)

                drawContext.canvas.nativeCanvas.drawRoundRect(
                    rectLeft,
                    rectTop,
                    rectRight,
                    rectBottom,
                    pathCornerRadius, // Use adjusted corner radius for the path
                    pathCornerRadius,
                    paint
                )
            }
        }
    }
}