package com.chavesdev.rollit.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.chavesdev.rollit.hardware.ShakeDetector

/**
 * Sensor Lifecycle Integration Composable effect that listens to accelerometer physical shake gestures
 * using [ShakeDetector] and triggers [onShake] when detected.
 */
@Composable
fun ShakeSensorEffect(
    isShakeEnabled: Boolean,
    sensitivity: Float,
    onShake: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(isShakeEnabled, sensitivity, context) {
        if (!isShakeEnabled) {
            onDispose { }
        } else {
            val shakeDetector = ShakeDetector(context).apply {
                shakeThresholdGravity = sensitivity
                setOnShakeListener {
                    currentOnShake()
                }
            }

            shakeDetector.start()

            onDispose {
                shakeDetector.stop()
            }
        }
    }
}
