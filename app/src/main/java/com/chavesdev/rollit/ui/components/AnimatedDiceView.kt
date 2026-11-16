package com.chavesdev.rollit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult
import com.chavesdev.rollit.ui.theme.RollitTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive Compose Animated Dice Box component supporting D4, D6, D8, D10, D12, D20, D100.
 *
 * Renders custom pip graphics for D6 and polyhedral geometric badges for multi-sided dice.
 * Supports swipe and tap gestures for rolling, along with 3D rotation, scaling bounce, and vibration animations.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimatedDiceView(
    diceType: DiceType,
    quantity: Int,
    isRolling: Boolean,
    rollingValues: List<Int>,
    currentResult: RollResult?,
    onSwipeRoll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Animation transitions for rolling state
    val infiniteTransition = rememberInfiniteTransition(label = "DiceRollingAnimation")

    val rollRotationX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationX"
    )

    val rollRotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 280, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceScale"
    )

    val wobbleZ by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobbleZ"
    )

    // Animated scale when idle vs rolling
    val animatedScale by animateFloatAsState(
        targetValue = if (isRolling) bounceScale else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "idleScale"
    )

    val targetRotationX = if (isRolling) rollRotationX else 0f
    val targetRotationY = if (isRolling) rollRotationY else 0f
    val targetRotationZ = if (isRolling) wobbleZ else 0f

    val valuesToDisplay = remember(isRolling, rollingValues, currentResult, quantity, diceType) {
        if (isRolling && rollingValues.isNotEmpty()) {
            rollingValues
        } else if (currentResult != null && currentResult.diceType == diceType && currentResult.quantity == quantity) {
            currentResult.rolls
        } else {
            List(quantity) { diceType.sides }
        }
    }

    val isCritHit = !isRolling && currentResult != null && currentResult.isCriticalHit
    val isCritFail = !isRolling && currentResult != null && currentResult.isCriticalFail

    val containerColor = when {
        isCritHit -> MaterialTheme.colorScheme.primaryContainer
//        isCritFail -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .pointerInput(isRolling) {
                detectDragGestures { change, dragAmount ->
                    if (!isRolling && (dragAmount.y.coerceAtLeast(0f) > 10f || dragAmount.x != 0f)) {
                        change.consume()
                        onSwipeRoll()
                    }
                }
            }
            .pointerInput(isRolling) {
                detectTapGestures {
                    if (!isRolling) {
                        onSwipeRoll()
                    }
                }
            },
//        shape = RoundedCornerShape(24.dp),
//        colors = CardDefaults.cardColors(containerColor = containerColor),
//        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header showing dice type badge
//            Surface(
//                shape = RoundedCornerShape(50),
//                color = MaterialTheme.colorScheme.primary,
//                contentColor = MaterialTheme.colorScheme.onPrimary,
//                shadowElevation = 2.dp
//            ) {
//                Text(
//                    text = "${quantity}x ${diceType.displayName} (${diceType.sides} sides)",
//                    style = MaterialTheme.typography.labelLarge.copy(
//                        fontWeight = FontWeight.Bold,
//                        letterSpacing = 1.sp
//                    ),
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
//                )
//            }

//            Spacer(modifier = Modifier.height(20.dp))

            // Dice Display Box with Graphics & Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        rotationX = targetRotationX
                        rotationY = targetRotationY
                        rotationZ = targetRotationZ
                        cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.Center
            ) {
//                if (quantity == 1) {
                SingleDieView(
                    diceType = diceType,
                    value = valuesToDisplay.firstOrNull() ?: diceType.sides,
                    isCritHit = isCritHit,
                    isCritFail = isCritFail,
                    size = 180.dp
                )
//                } else {
//                    FlowRow(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalArrangement = Arrangement.Center,
//                        maxItemsInEachRow = 4
//                    ) {
//                        valuesToDisplay.forEachIndexed { index, valForDie ->
//                            Box(modifier = Modifier.padding(6.dp)) {
//                                SingleDieView(
//                                    diceType = diceType,
//                                    value = valForDie,
//                                    isCritHit = isCritHit,
//                                    isCritFail = isCritFail,
//                                    size = if (quantity <= 4) 90.dp else 70.dp
//                                )
//                            }
//                        }
//                    }
//                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result announcement / instruction prompt
            when {
                isRolling -> {
                    Text(
                        text = "Rolling...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                currentResult == null && !isRolling -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TouchApp,
                            contentDescription = "Swipe or tap to roll",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Swipe, tap dice or shake your phone to roll",//TODO extract and translate to pt-BR
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
//            if (isRolling) {
//                Text(
//                    text = "Rolling...",
//                    style = MaterialTheme.typography.titleMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                )
//            }
//            else if (currentResult != null) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Total: ",
//                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
//                        )
//                        Text(
//                            text = "${currentResult.total}",
//                            style = MaterialTheme.typography.headlineLarge.copy(
//                                fontWeight = FontWeight.ExtraBold,
//                                color = when {
//                                    isCritHit -> MaterialTheme.colorScheme.primary
//                                    isCritFail -> MaterialTheme.colorScheme.error
//                                    else -> MaterialTheme.colorScheme.onSurface
//                                }
//                            )
//                        )
//                    }
//
//                    if (currentResult.modifier != 0 || currentResult.quantity > 1) {
//                        Text(
//                            text = "${currentResult.formula} = ${currentResult.formattedRolls}${if (currentResult.modifier != 0) " + (${currentResult.modifier})" else ""}",
//                            style = MaterialTheme.typography.bodyMedium.copy(
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        )
//                    }
//
////                    if (isCritHit) {
////                        Text(
////                            text = "✨ CRITICAL HIT! ✨",
////                            style = MaterialTheme.typography.labelLarge.copy(
////                                fontWeight = FontWeight.Bold,
////                                color = MaterialTheme.colorScheme.primary
////                            ),
////                            modifier = Modifier.padding(top = 4.dp)
////                        )
////                    } else if (isCritFail) {
////                        Text(
////                            text = "💀 CRITICAL FAIL! 💀",
////                            style = MaterialTheme.typography.labelLarge.copy(
////                                fontWeight = FontWeight.Bold,
////                                color = MaterialTheme.colorScheme.error
////                            ),
////                            modifier = Modifier.padding(top = 4.dp)
////                        )
////                    }
//                }
//            } else {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.TouchApp,
//                        contentDescription = "Swipe or tap to roll",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(18.dp)
//                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        text = "Swipe, tap dice or shake your phone to roll",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    )
//                }
//            }
        }
    }
}

/**
 * Component representing a single die visualization for [diceType].
 */
@Composable
fun SingleDieView(
    diceType: DiceType,
    value: Int,
    isCritHit: Boolean,
    isCritFail: Boolean,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val critHitColor = Color(0xFFFFD700) // Gold
    val critFailColor = MaterialTheme.colorScheme.error

    val dieBgColor = when {
        isCritHit -> critHitColor
        isCritFail -> critFailColor
        else -> primaryColor
    }

    val textColor = when {
        isCritHit -> Color.Black
        isCritFail -> Color.White
        else -> MaterialTheme.colorScheme.onPrimary
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (diceType == DiceType.D6) {
            // Draw custom Canvas D6 Pip graphics
            Canvas(modifier = Modifier.size(size)) {
                drawD6Die(
                    value = value,
                    dieBgColor = dieBgColor,
                    pipColor = textColor
                )
            }
        } else {
            // Draw polyhedral geometric dice badge with Canvas background + number text overlay
            Canvas(modifier = Modifier.size(size)) {
                drawPolyhedralDie(
                    diceType = diceType,
                    dieBgColor = dieBgColor,
                    strokeColor = textColor.copy(alpha = 0.5f)
                )
            }

            Text(
                text = value.toString(),
                style = when {
                    size >= 120.dp -> MaterialTheme.typography.headlineLarge
                    size >= 80.dp -> MaterialTheme.typography.headlineMedium
                    else -> MaterialTheme.typography.titleLarge
                }.copy(
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

/**
 * Draws custom Canvas pip graphics for a 6-sided die.
 */
private fun DrawScope.drawD6Die(
    value: Int,
    dieBgColor: Color,
    pipColor: Color,
) {
    val sizePx = size.width
    val cornerRadiusPx = sizePx * 0.18f

    // Draw die body
    drawRoundRect(
        color = dieBgColor,
        size = Size(sizePx, sizePx),
        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
    )

    // Draw inner border highlight
    drawRoundRect(
        color = pipColor.copy(alpha = 0.25f),
        size = Size(sizePx, sizePx),
        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
        style = Stroke(width = sizePx * 0.03f)
    )

    // Pip dimensions
    val pipRadius = sizePx * 0.09f
    val cx = sizePx * 0.5f
    val cy = sizePx * 0.5f
    val left = sizePx * 0.26f
    val right = sizePx * 0.74f
    val top = sizePx * 0.26f
    val bottom = sizePx * 0.74f

    val pipPositions = when (value.coerceIn(1, 6)) {
        1 -> listOf(Offset(cx, cy))
        2 -> listOf(Offset(top, right), Offset(bottom, left))
        3 -> listOf(Offset(top, right), Offset(cx, cy), Offset(bottom, left))
        4 -> listOf(
            Offset(left, top),
            Offset(right, top),
            Offset(left, bottom),
            Offset(right, bottom)
        )

        5 -> listOf(
            Offset(left, top),
            Offset(right, top),
            Offset(cx, cy),
            Offset(left, bottom),
            Offset(right, bottom)
        )

        6 -> listOf(
            Offset(left, top), Offset(right, top),
            Offset(left, cy), Offset(right, cy),
            Offset(left, bottom), Offset(right, bottom)
        )

        else -> listOf(Offset(cx, cy))
    }

    pipPositions.forEach { position ->
        drawCircle(
            color = pipColor,
            radius = pipRadius,
            center = position
        )
    }
}

/**
 * Draws polyhedral geometric dice badges on Canvas for D4, D8, D10, D12, D20, D100.
 */
private fun DrawScope.drawPolyhedralDie(
    diceType: DiceType,
    dieBgColor: Color,
    strokeColor: Color,
) {
    val sizePx = size.width
    val center = Offset(sizePx / 2f, sizePx / 2f)
    val radius = sizePx * 0.45f

    val path = Path()

    when (diceType) {
        DiceType.D4 -> {
            // Triangle
            val top = Offset(center.x, center.y - radius)
            val bottomLeft = Offset(center.x - radius * 0.866f, center.y + radius * 0.5f)
            val bottomRight = Offset(center.x + radius * 0.866f, center.y + radius * 0.5f)

            path.moveTo(top.x, top.y)
            path.lineTo(bottomRight.x, bottomRight.y)
            path.lineTo(bottomLeft.x, bottomLeft.y)
            path.close()

            drawPath(path = path, color = dieBgColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = sizePx * 0.035f))

            // Inner lines
            drawLine(strokeColor, top, center, strokeWidth = sizePx * 0.02f)
            drawLine(strokeColor, bottomLeft, center, strokeWidth = sizePx * 0.02f)
            drawLine(strokeColor, bottomRight, center, strokeWidth = sizePx * 0.02f)
        }

        DiceType.D8 -> {
            // Octahedron Diamond
            val top = Offset(center.x, center.y - radius)
            val bottom = Offset(center.x, center.y + radius)
            val left = Offset(center.x - radius * 0.85f, center.y)
            val right = Offset(center.x + radius * 0.85f, center.y)

            path.moveTo(top.x, top.y)
            path.lineTo(right.x, right.y)
            path.lineTo(bottom.x, bottom.y)
            path.lineTo(left.x, left.y)
            path.close()

            drawPath(path = path, color = dieBgColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = sizePx * 0.035f))

            // Inner facet lines
            drawLine(strokeColor, top, bottom, strokeWidth = sizePx * 0.02f)
            drawLine(strokeColor, left, right, strokeWidth = sizePx * 0.02f)
        }

        DiceType.D10 -> {
            // Decahedron Kite Shield
            val top = Offset(center.x, center.y - radius)
            val rightUpper = Offset(center.x + radius * 0.8f, center.y - radius * 0.2f)
            val bottom = Offset(center.x, center.y + radius)
            val leftUpper = Offset(center.x - radius * 0.8f, center.y - radius * 0.2f)

            path.moveTo(top.x, top.y)
            path.lineTo(rightUpper.x, rightUpper.y)
            path.lineTo(bottom.x, bottom.y)
            path.lineTo(leftUpper.x, leftUpper.y)
            path.close()

            drawPath(path = path, color = dieBgColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = sizePx * 0.035f))

            drawLine(strokeColor, top, bottom, strokeWidth = sizePx * 0.02f)
            drawLine(strokeColor, leftUpper, rightUpper, strokeWidth = sizePx * 0.02f)
        }

        DiceType.D12 -> {
            // Regular Pentagon
            val numSides = 5
            for (i in 0 until numSides) {
                val angle = (2 * PI / numSides * i - PI / 2).toFloat()
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(path = path, color = dieBgColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = sizePx * 0.035f))

            // Inner pentagon facet
            val innerPath = Path()
            val innerRadius = radius * 0.45f
            for (i in 0 until numSides) {
                val angle = (2 * PI / numSides * i - PI / 2).toFloat()
                val x = center.x + innerRadius * cos(angle)
                val y = center.y + innerRadius * sin(angle)
                if (i == 0) innerPath.moveTo(x, y) else innerPath.lineTo(x, y)
            }
            innerPath.close()
            drawPath(path = innerPath, color = strokeColor, style = Stroke(width = sizePx * 0.02f))
        }

        DiceType.D20 -> {
            // Icosahedron Hexagon + Inner Facet Triangles
            val numSides = 6
            val vertices = mutableListOf<Offset>()
            for (i in 0 until numSides) {
                val angle = (2 * PI / numSides * i - PI / 6).toFloat()
                val x = center.x + radius * cos(angle)
                val y = center.y + radius * sin(angle)
                vertices.add(Offset(x, y))
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(path = path, color = dieBgColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = sizePx * 0.035f))

            // Draw inner triangle facet connections (vertices 0, 2, 4)
            if (vertices.size == 6) {
                val innerTriangle = Path()
                innerTriangle.moveTo(vertices[0].x, vertices[0].y)
                innerTriangle.lineTo(vertices[2].x, vertices[2].y)
                innerTriangle.lineTo(vertices[4].x, vertices[4].y)
                innerTriangle.close()
                drawPath(
                    path = innerTriangle,
                    color = strokeColor,
                    style = Stroke(width = sizePx * 0.025f)
                )

                // Connect alternate vertices to inner triangle corners
                drawLine(strokeColor, vertices[1], vertices[0], strokeWidth = sizePx * 0.02f)
                drawLine(strokeColor, vertices[3], vertices[2], strokeWidth = sizePx * 0.02f)
                drawLine(strokeColor, vertices[5], vertices[4], strokeWidth = sizePx * 0.02f)
            }
        }

        DiceType.D100 -> {
            // Sphere / Circle badge with dual concentric rings
            drawCircle(color = dieBgColor, radius = radius, center = center)
            drawCircle(
                color = strokeColor,
                radius = radius,
                center = center,
                style = Stroke(width = sizePx * 0.035f)
            )
            drawCircle(
                color = strokeColor,
                radius = radius * 0.75f,
                center = center,
                style = Stroke(width = sizePx * 0.02f)
            )
        }

        else -> {
            drawCircle(color = dieBgColor, radius = radius, center = center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedDiceViewPreview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = null,
            onSwipeRoll = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedDiceView1Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(1),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedDiceView2Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(2),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AnimatedDiceView3Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(3),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AnimatedDiceView4Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(4),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AnimatedDiceView5Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(5),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AnimatedDiceView6Preview() {
    RollitTheme {
        AnimatedDiceView(
            diceType = DiceType.D6,
            quantity = 1,
            isRolling = false,
            rollingValues = emptyList(),
            currentResult = RollResult(
                diceType = DiceType.D6,
                quantity = 1,
                rolls = listOf(6),
                modifier = 0
            ),
            onSwipeRoll = {}
        )
    }
}
