package com.chavesdev.rollit.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chavesdev.rollit.R
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult
import com.chavesdev.rollit.ui.theme.RollitTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interactive Compose Animated 3D Dice Box supporting D4, D6, D8, D10, D12, D20, D100.
 *
 * Renders realistic 3D cube perspective for D6 and elevated 3D emblem gem/metallic gradients for polyhedral dice.
 * Supports smooth 3D tilt/rotation feedback during swipe/scroll gestures and continuous rolling animations.
 */
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
    // User interaction 3D tilt state
    var userRotationX by remember { mutableStateOf(0f) }
    var userRotationY by remember { mutableStateOf(0f) }

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
        initialValue = 0.88f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceScale"
    )

    val wobbleZ by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 140, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobbleZ"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isRolling) bounceScale else 1.0f,
        animationSpec = spring(dampingRatio = 0.55f),
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

    Box(
        modifier = modifier
            .fillMaxSize()
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
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Immersive Fullscreen Prominent 3D Dice Display
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
                SingleDieView(
                    diceType = diceType,
                    value = valuesToDisplay.firstOrNull() ?: diceType.sides,
                    isCritHit = isCritHit,
                    isCritFail = isCritFail,
                    size = 180.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result announcement / instruction prompt with smooth modern card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when {
                        isRolling -> {
                            Text(
                                text = stringResource(R.string.rolling),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.5.sp
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
                                    contentDescription = stringResource(R.string.swipe_or_tap_to_roll),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.roll_instructions),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                        currentResult != null && !isRolling -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.total_label),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${currentResult.total}",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = when {
                                                isCritHit -> Color(0xFFFFD700)
                                                isCritFail -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Component representing a single enhanced 3D die visualization with shading, bevels, and gem gradients.
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
    val critHitColor = Color(0xFFFFD700) // Gold
    val critFailColor = MaterialTheme.colorScheme.error

    val dieBgColor = when {
        isCritHit -> critHitColor
        isCritFail -> critFailColor
        else -> primaryColor
    }

    val textColor = when {
        isCritHit -> Color(0xFF1E1E1E)
        isCritFail -> Color.White
        else -> MaterialTheme.colorScheme.onPrimary
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (diceType == DiceType.D6) {
            // Enhanced 3D Cube perspective with shading gradients, bevels, and distinct face illumination
            Canvas(modifier = Modifier.size(size)) {
                drawD6Die(
                    value = value,
                    dieBgColor = dieBgColor,
                    pipColor = textColor
                )
            }
        } else {
            // Elevated 3D emblem appearance for polyhedral dice with metallic/gem gradients and typography
//            Canvas(modifier = Modifier.size(size)) {
//                drawEnhanced3DPolyhedralDie(
//                    diceType = diceType,
//                    dieBgColor = dieBgColor,
//                    strokeColor = textColor
//                )
//            }
//
//            Text(
//                text = value.toString(),
//                style = when {
//                    size >= 180.dp -> MaterialTheme.typography.displaySmall
//                    size >= 120.dp -> MaterialTheme.typography.headlineLarge
//                    else -> MaterialTheme.typography.headlineMedium
//                }.copy(
//                    fontWeight = FontWeight.Black,
//                    color = textColor,
//                    textAlign = TextAlign.Center
//                )
//            )
        }
    }
}

/**
 * Draws enhanced 3D D6 cube perspective with multi-face shading gradients, bevels, and face illumination.
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
        // Pip inner gem glow
        drawCircle(
            color = pipColor,
            radius = pipRadius,
            center = position
        )
        // Pip specular highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = pipRadius * 0.35f,
            center = Offset(position.x - pipRadius * 0.3f, position.y - pipRadius * 0.3f)
        )
    }
}

/**
 * Draws elevated 3D polyhedral dice emblem appearance with metallic/gem gradients and facet lines.
 */
//private fun DrawScope.drawEnhanced3DPolyhedralDie(
//    diceType: DiceType,
//    dieBgColor: Color,
//    strokeColor: Color,
//) {
//    val sizePx = size.width
//    val center = Offset(sizePx / 2f, sizePx / 2f)
//    val radius = sizePx * 0.46f
//
//    // Gem / Metallic radial gradient for elevated 3D emblem appearance
//    val gemGradient = Brush.radialGradient(
//        colors = listOf(
//            dieBgColor.copy(alpha = 1.0f),
//            dieBgColor.copy(alpha = 0.85f),
//            dieBgColor.copy(alpha = 0.60f)
//        ),
//        center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
//        radius = radius * 1.4f
//    )
//
//    val path = Path()
//
//    when (diceType) {
//        DiceType.D4 -> {
//            val top = Offset(center.x, center.y - radius)
//            val bottomLeft = Offset(center.x - radius * 0.866f, center.y + radius * 0.5f)
//            val bottomRight = Offset(center.x + radius * 0.866f, center.y + radius * 0.5f)
//
//            path.moveTo(top.x, top.y)
//            path.lineTo(bottomRight.x, bottomRight.y)
//            path.lineTo(bottomLeft.x, bottomLeft.y)
//            path.close()
//
//            drawPath(path = path, brush = gemGradient)
//            drawPath(path = path, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = sizePx * 0.04f))
//
//            drawLine(strokeColor.copy(alpha = 0.6f), top, center, strokeWidth = sizePx * 0.025f)
//            drawLine(strokeColor.copy(alpha = 0.6f), bottomLeft, center, strokeWidth = sizePx * 0.025f)
//            drawLine(strokeColor.copy(alpha = 0.6f), bottomRight, center, strokeWidth = sizePx * 0.025f)
//        }
//
//        DiceType.D8 -> {
//            val top = Offset(center.x, center.y - radius)
//            val bottom = Offset(center.x, center.y + radius)
//            val left = Offset(center.x - radius * 0.85f, center.y)
//            val right = Offset(center.x + radius * 0.85f, center.y)
//
//            path.moveTo(top.x, top.y)
//            path.lineTo(right.x, right.y)
//            path.lineTo(bottom.x, bottom.y)
//            path.lineTo(left.x, left.y)
//            path.close()
//
//            drawPath(path = path, brush = gemGradient)
//            drawPath(path = path, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = sizePx * 0.04f))
//
//            drawLine(strokeColor.copy(alpha = 0.6f), top, bottom, strokeWidth = sizePx * 0.025f)
//            drawLine(strokeColor.copy(alpha = 0.6f), left, right, strokeWidth = sizePx * 0.025f)
//        }
//
//        DiceType.D10 -> {
//            val top = Offset(center.x, center.y - radius)
//            val rightUpper = Offset(center.x + radius * 0.8f, center.y - radius * 0.2f)
//            val bottom = Offset(center.x, center.y + radius)
//            val leftUpper = Offset(center.x - radius * 0.8f, center.y - radius * 0.2f)
//
//            path.moveTo(top.x, top.y)
//            path.lineTo(rightUpper.x, rightUpper.y)
//            path.lineTo(bottom.x, bottom.y)
//            path.lineTo(leftUpper.x, leftUpper.y)
//            path.close()
//
//            drawPath(path = path, brush = gemGradient)
//            drawPath(path = path, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = sizePx * 0.04f))
//
//            drawLine(strokeColor.copy(alpha = 0.6f), top, bottom, strokeWidth = sizePx * 0.025f)
//            drawLine(strokeColor.copy(alpha = 0.6f), leftUpper, rightUpper, strokeWidth = sizePx * 0.025f)
//        }
//
//        DiceType.D12 -> {
//            val numSides = 5
//            for (i in 0 until numSides) {
//                val angle = (2 * PI / numSides * i - PI / 2).toFloat()
//                val x = center.x + radius * cos(angle)
//                val y = center.y + radius * sin(angle)
//                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
//            }
//            path.close()
//
//            drawPath(path = path, brush = gemGradient)
//            drawPath(path = path, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = sizePx * 0.04f))
//
//            val innerPath = Path()
//            val innerRadius = radius * 0.45f
//            for (i in 0 until numSides) {
//                val angle = (2 * PI / numSides * i - PI / 2).toFloat()
//                val x = center.x + innerRadius * cos(angle)
//                val y = center.y + innerRadius * sin(angle)
//                if (i == 0) innerPath.moveTo(x, y) else innerPath.lineTo(x, y)
//            }
//            innerPath.close()
//            drawPath(path = innerPath, color = strokeColor.copy(alpha = 0.6f), style = Stroke(width = sizePx * 0.025f))
//        }
//
//        DiceType.D20 -> {
//            val numSides = 6
//            val vertices = mutableListOf<Offset>()
//            for (i in 0 until numSides) {
//                val angle = (2 * PI / numSides * i - PI / 6).toFloat()
//                val x = center.x + radius * cos(angle)
//                val y = center.y + radius * sin(angle)
//                vertices.add(Offset(x, y))
//                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
//            }
//            path.close()
//
//            drawPath(path = path, brush = gemGradient)
//            drawPath(path = path, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = sizePx * 0.04f))
//
//            if (vertices.size == 6) {
//                val innerTriangle = Path()
//                innerTriangle.moveTo(vertices[0].x, vertices[0].y)
//                innerTriangle.lineTo(vertices[2].x, vertices[2].y)
//                innerTriangle.lineTo(vertices[4].x, vertices[4].y)
//                innerTriangle.close()
//                drawPath(
//                    path = innerTriangle,
//                    color = strokeColor.copy(alpha = 0.6f),
//                    style = Stroke(width = sizePx * 0.025f)
//                )
//
//                drawLine(strokeColor.copy(alpha = 0.6f), vertices[1], vertices[0], strokeWidth = sizePx * 0.025f)
//                drawLine(strokeColor.copy(alpha = 0.6f), vertices[3], vertices[2], strokeWidth = sizePx * 0.025f)
//                drawLine(strokeColor.copy(alpha = 0.6f), vertices[5], vertices[4], strokeWidth = sizePx * 0.025f)
//            }
//        }
//
//        DiceType.D100 -> {
//            drawCircle(brush = gemGradient, radius = radius, center = center)
//            drawCircle(
//                color = Color.White.copy(alpha = 0.7f),
//                radius = radius,
//                center = center,
//                style = Stroke(width = sizePx * 0.04f)
//            )
//            drawCircle(
//                color = strokeColor.copy(alpha = 0.6f),
//                radius = radius * 0.75f,
//                center = center,
//                style = Stroke(width = sizePx * 0.025f)
//            )
//        }
//
//        else -> {
//            drawCircle(brush = gemGradient, radius = radius, center = center)
//        }
//    }
//}

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
