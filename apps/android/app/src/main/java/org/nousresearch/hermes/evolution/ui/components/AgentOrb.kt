package org.nousresearch.hermes.evolution.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.nousresearch.hermes.evolution.engine.AgentState
import org.nousresearch.hermes.evolution.ui.theme.AmberWaiting
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.ElectricIndigo
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SlateOffline
import org.nousresearch.hermes.evolution.ui.theme.VioletNebula
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Futuristic Agent Orb depicting the 7 Hermes agent states:
 * READY, THINKING, WORKING, WAITING, COMPLETED, ERROR, OFFLINE.
 */
@Composable
fun AgentOrb(
    state: AgentState,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbTransitions")

    // Pulsing core breathing
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = when (state) {
                AgentState.THINKING -> 800
                AgentState.WORKING -> 600
                AgentState.WAITING -> 1000
                AgentState.ERROR -> 400
                else -> 1800
            }, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulse"
    )

    // Orbital ring rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == AgentState.WORKING) 3000 else 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    val primaryColor = when (state) {
        AgentState.READY -> EmeraldReady
        AgentState.THINKING -> ElectricIndigo
        AgentState.WORKING -> CyanGlow
        AgentState.WAITING -> AmberWaiting
        AgentState.COMPLETED -> EmeraldReady
        AgentState.ERROR -> RoseError
        AgentState.OFFLINE -> SlateOffline
    }

    val secondaryColor = when (state) {
        AgentState.READY -> CyanGlow
        AgentState.THINKING -> VioletNebula
        AgentState.WORKING -> ElectricIndigo
        AgentState.WAITING -> AmberWaiting.copy(alpha = 0.6f)
        AgentState.COMPLETED -> CyanGlow
        AgentState.ERROR -> RoseError.copy(alpha = 0.5f)
        AgentState.OFFLINE -> SlateOffline.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            val baseRadius = size.toPx() * 0.28f
            val currentRadius = baseRadius * pulse

            // 1. Soft Outer Atmospheric Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        secondaryColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.8f
                ),
                radius = baseRadius * 1.8f,
                center = center
            )

            // 2. Rotating Kinetic Orbital Rings
            val ringRadius = baseRadius * 1.35f
            drawCircle(
                color = secondaryColor.copy(alpha = 0.4f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Dynamic orbital particles on ring
            val angleRad = Math.toRadians(rotation.toDouble())
            val px = center.x + ringRadius * cos(angleRad).toFloat()
            val py = center.y + ringRadius * sin(angleRad).toFloat()
            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(px, py)
            )

            val oppAngleRad = Math.toRadians((rotation + 180.0) % 360.0)
            val oppX = center.x + ringRadius * cos(oppAngleRad).toFloat()
            val oppY = center.y + ringRadius * sin(oppAngleRad).toFloat()
            drawCircle(
                color = secondaryColor,
                radius = 3.dp.toPx(),
                center = Offset(oppX, oppY)
            )

            // 3. Dense Luminescent Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        secondaryColor
                    ),
                    center = center,
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = center
            )
        }
    }
}
