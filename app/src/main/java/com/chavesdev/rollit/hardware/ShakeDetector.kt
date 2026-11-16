package com.chavesdev.rollit.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Helper class using Android's [SensorManager] accelerometer to detect physical device shake gestures.
 */
class ShakeDetector(
    private val sensorManager: SensorManager,
) : SensorEventListener {

    fun interface OnShakeListener {
        fun onShake()
    }

    constructor(context: Context) : this(
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    )

    private var listener: OnShakeListener? = null
    private var lastShakeTimestamp: Long = 0L
    private var isListeningState: Boolean = false

    /**
     * Minimum g-force acceleration required to trigger a shake event.
     * Default is 2.5f (2.5x earth gravity).
     */
    var shakeThresholdGravity: Float = DEFAULT_SHAKE_THRESHOLD_GRAVITY

    /**
     * Minimum time gap in milliseconds between consecutive shake detections to prevent accidental duplicate triggers.
     */
    var minIntervalMs: Long = DEFAULT_MIN_INTERVAL_MS

    val isListening: Boolean
        get() = isListeningState

    fun setOnShakeListener(listener: OnShakeListener?) {
        this.listener = listener
    }

    fun setOnShakeListener(onShake: () -> Unit) {
        this.listener = OnShakeListener { onShake() }
    }

    /**
     * Starts listening for accelerometer events.
     * Returns true if accelerometer is available and listener registered successfully.
     */
    fun start(): Boolean {
        if (isListeningState) return true

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return false
        val registered = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        isListeningState = registered
        return registered
    }

    /**
     * Stops listening for sensor events to conserve battery.
     */
    fun stop() {
        if (!isListeningState) return
        sensorManager.unregisterListener(this)
        isListeningState = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        // gForce is 1.0 when device is static
        val gForce = sqrt((gX * gX) + (gY * gY) + (gZ * gZ))

        if (gForce > shakeThresholdGravity) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimestamp >= minIntervalMs) {
                lastShakeTimestamp = now
                listener?.onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    companion object {
        const val DEFAULT_SHAKE_THRESHOLD_GRAVITY = 2.5f
        const val DEFAULT_MIN_INTERVAL_MS = 500L
    }
}
