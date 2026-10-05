package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantViolet
import kotlin.math.sin

enum class OrbState {
    IDLE,
    THINKING,
    SPEAKING,
    SLEEPING
}

@Composable
fun GlowingOrb(
    state: OrbState = OrbState.IDLE,
    size: Dp = 100.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbTransition")

    // Breathing pulse
    val pulseDuration = when (state) {
        OrbState.THINKING -> 800
        OrbState.SPEAKING -> 600
        OrbState.SLEEPING -> 3200
        OrbState.IDLE -> 1800
    }

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (state == OrbState.SLEEPING) 0.88f else 0.92f,
        targetValue = if (state == OrbState.SLEEPING) 1.02f else 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Continuous rotation
    val rotationSpeed = when (state) {
        OrbState.THINKING -> 2000
        OrbState.SPEAKING -> 3000
        OrbState.SLEEPING -> 12000
        OrbState.IDLE -> 6000
    }

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationSpeed, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    // Wave ring radius
    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == OrbState.SPEAKING) 1000 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveProgress"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .testTag("glowing_orb")
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.58f

            // Determine palette based on state
            val outerGlowColor = when (state) {
                OrbState.SLEEPING -> Color(0xFF38BDF8).copy(alpha = 0.25f)
                OrbState.THINKING -> GlowingAmber.copy(alpha = 0.6f)
                OrbState.SPEAKING -> GlowingRose.copy(alpha = 0.7f)
                OrbState.IDLE -> RadiantViolet.copy(alpha = 0.5f)
            }

            val primaryGradColor = when (state) {
                OrbState.SLEEPING -> Color(0xFF1E293B)
                OrbState.THINKING -> RadiantViolet
                OrbState.SPEAKING -> GlowingRose
                OrbState.IDLE -> RadiantViolet
            }

            val secondaryGradColor = when (state) {
                OrbState.SLEEPING -> Color(0xFF0F172A)
                OrbState.THINKING -> RadiantCyan
                OrbState.SPEAKING -> RadiantCyan
                OrbState.IDLE -> RadiantCyan
            }

            // Outer acoustic wave ring
            if (state != OrbState.SLEEPING) {
                val waveAlpha = (1f - (waveProgress - 0.5f) / 0.8f).coerceIn(0f, 0.7f)
                drawCircle(
                    color = outerGlowColor.copy(alpha = waveAlpha),
                    radius = baseRadius * waveProgress * pulseScale,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Diffuse Ambient Aura Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        outerGlowColor,
                        outerGlowColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.55f * pulseScale
                ),
                radius = baseRadius * 1.55f * pulseScale,
                center = center
            )

            // Dynamic Rotating Core Orb
            rotate(rotationAngle, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryGradColor,
                            secondaryGradColor,
                            if (state == OrbState.SLEEPING) Color(0xFF38BDF8) else GlowingRose,
                            primaryGradColor
                        ),
                        center = center
                    ),
                    radius = baseRadius * pulseScale,
                    center = center
                )

                // Concentric inner orbital ring
                drawCircle(
                    color = Color.White.copy(alpha = if (state == OrbState.SLEEPING) 0.2f else 0.45f),
                    radius = baseRadius * 0.75f * pulseScale,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Soft white inner specular highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (state == OrbState.SLEEPING) 0.3f else 0.75f),
                        Color.Transparent
                    ),
                    center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.25f),
                    radius = baseRadius * 0.55f
                ),
                radius = baseRadius * 0.55f,
                center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.25f)
            )
        }
    }
}
