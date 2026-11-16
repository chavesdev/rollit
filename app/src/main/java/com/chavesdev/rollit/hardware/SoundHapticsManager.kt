package com.chavesdev.rollit.hardware

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.sin
import kotlin.random.Random

/**
 * Manages low-latency synthesized audio effects and haptic feedback for dice rolls and UI interactions.
 */
class SoundHapticsManager(context: Context) {

    private val appContext = context.applicationContext

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    private val clickTrack: AudioTrack? by lazy { createAudioTrack(generateClickPcm()) }
    private val rollTrack: AudioTrack? by lazy { createAudioTrack(generateRollPcm()) }
    private val critHitTrack: AudioTrack? by lazy { createAudioTrack(generateCritHitPcm()) }
    private val critFailTrack: AudioTrack? by lazy { createAudioTrack(generateCritFailPcm()) }

    // --- Audio API ---

    fun playClickSound() {
        if (!isSoundEnabled) return
        playStaticTrack(clickTrack)
    }

    fun playRollSound() {
        if (!isSoundEnabled) return
        playStaticTrack(rollTrack)
    }

    fun playCriticalHitSound() {
        if (!isSoundEnabled) return
        playStaticTrack(critHitTrack)
    }

    fun playCriticalFailSound() {
        if (!isSoundEnabled) return
        playStaticTrack(critFailTrack)
    }

    // --- Haptics API ---

    fun vibrateClick() {
        if (!isHapticsEnabled || (vibrator?.hasVibrator() != true)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15L)
        }
    }

    fun vibrateRoll() {
        if (!isHapticsEnabled || (vibrator?.hasVibrator() != true)) return

        val pattern = longArrayOf(0, 25, 30, 20, 35, 15)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitudes = intArrayOf(0, 180, 0, 150, 0, 100)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    fun vibrateCriticalHit() {
        if (!isHapticsEnabled || (vibrator?.hasVibrator() != true)) return

        val pattern = longArrayOf(0, 60, 40, 90)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitudes = intArrayOf(0, 255, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    fun vibrateCriticalFail() {
        if (!isHapticsEnabled || (vibrator?.hasVibrator() != true)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(180L, 120))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(180L)
        }
    }

    // --- Combined Feedback API ---

    fun playClickFeedback() {
        playClickSound()
        vibrateClick()
    }

    fun playRollFeedback() {
        playRollSound()
        vibrateRoll()
    }

    fun playCriticalHitFeedback() {
        playCriticalHitSound()
        vibrateCriticalHit()
    }

    fun playCriticalFailFeedback() {
        playCriticalFailSound()
        vibrateCriticalFail()
    }

    fun release() {
        safelyReleaseTrack(clickTrack)
        safelyReleaseTrack(rollTrack)
        safelyReleaseTrack(critHitTrack)
        safelyReleaseTrack(critFailTrack)
    }

    // --- PCM Synthesis Helpers ---

    private fun playStaticTrack(track: AudioTrack?) {
        track?.let {
            try {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.reloadStaticData()
                it.play()
            } catch (_: Exception) {
            }
        }
    }

    private fun safelyReleaseTrack(track: AudioTrack?) {
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {
        }
    }

    private fun createAudioTrack(pcmData: ShortArray): AudioTrack? {
        return try {
            val bufferSize = pcmData.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmData, 0, pcmData.size)
            track
        } catch (_: Exception) {
            null
        }
    }

    private fun generateClickPcm(): ShortArray {
        val durationMs = 12
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val decay = (1.0f - progress) * (1.0f - progress)
            val noise = (Random.nextFloat() * 2.0f - 1.0f) * 0.8f
            val tone = sin(2.0 * Math.PI * 1200.0 * i / SAMPLE_RATE).toFloat() * 0.2f
            samples[i] = ((noise + tone) * decay * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateRollPcm(): ShortArray {
        val durationMs = 280
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val samples = ShortArray(numSamples)
        val clickTimesMs = intArrayOf(0, 40, 85, 135, 190, 240)
        val random = Random(42)

        for (clickTime in clickTimesMs) {
            val startIdx = (SAMPLE_RATE * clickTime) / 1000
            val clickDurationSamples = (SAMPLE_RATE * 15) / 1000
            val endIdx = (startIdx + clickDurationSamples).coerceAtMost(numSamples)
            val frequency = 800.0 + random.nextInt(600)

            for (i in startIdx until endIdx) {
                val relIdx = i - startIdx
                val progress = relIdx.toFloat() / clickDurationSamples
                val decay = 1.0f - progress
                val noise = (random.nextFloat() * 2.0f - 1.0f) * 0.7f
                val tone = sin(2.0 * Math.PI * frequency * relIdx / SAMPLE_RATE).toFloat() * 0.3f
                val sampleValue = ((noise + tone) * decay * 0.7f * Short.MAX_VALUE).toInt()
                val current = samples[i].toInt()
                samples[i] = (current + sampleValue).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return samples
    }

    private fun generateCritHitPcm(): ShortArray {
        val durationMs = 350
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val samples = ShortArray(numSamples)
        val freq1 = 880.0 // A5
        val freq2 = 1320.0 // E6

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val decay = (1.0f - progress)
            val s1 = sin(2.0 * Math.PI * freq1 * i / SAMPLE_RATE)
            val s2 = sin(2.0 * Math.PI * freq2 * i / SAMPLE_RATE)
            val mixed = ((s1 + s2) * 0.4 * decay * Short.MAX_VALUE).toInt()
            samples[i] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateCritFailPcm(): ShortArray {
        val durationMs = 350
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val samples = ShortArray(numSamples)
        val freq = 110.0 // Low A2 buzz

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val decay = (1.0f - progress)
            val saw = ((i * freq / SAMPLE_RATE) % 1.0) * 2.0 - 1.0
            val sampleVal = (saw * 0.5 * decay * Short.MAX_VALUE).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    companion object {
        private const val SAMPLE_RATE = 44100
    }
}
