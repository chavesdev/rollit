package com.chavesdev.rollit.hardware

import android.hardware.SensorManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class ShakeDetectorTest {

    @Test
    fun testShakeThresholdDefaults() {
        assertEquals(2.5f, ShakeDetector.DEFAULT_SHAKE_THRESHOLD_GRAVITY, 0.001f)
        assertEquals(500L, ShakeDetector.DEFAULT_MIN_INTERVAL_MS)
    }

    @Test
    fun testGravityForceCalculation() {
        // Static device resting flat on table (z = 9.81m/s^2)
        val x = 0f
        val y = 0f
        val z = SensorManager.GRAVITY_EARTH

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        // Static device should equal 1.0g
        assertEquals(1.0f, gForce, 0.01f)
        assertFalse("Static 1.0g should not trigger shake threshold of 2.5g", gForce > ShakeDetector.DEFAULT_SHAKE_THRESHOLD_GRAVITY)

        // Violent shake event (e.g., 30m/s^2 on x axis)
        val shakeX = 30.0f
        val shakeGforce = sqrt((shakeX / SensorManager.GRAVITY_EARTH) * (shakeX / SensorManager.GRAVITY_EARTH))

        assertTrue("30m/s^2 (~3.05g) should trigger shake threshold", shakeGforce > ShakeDetector.DEFAULT_SHAKE_THRESHOLD_GRAVITY)
    }
}
