package com.chavesdev.rollit.hardware

import android.content.Context

/**
 * Convenience wrapper class around [SoundHapticsManager] providing audio & haptics feedback.
 */
class FeedbackManager(context: Context) {

    private val soundHapticsManager = SoundHapticsManager(context)

    var isSoundEnabled: Boolean
        get() = soundHapticsManager.isSoundEnabled
        set(value) {
            soundHapticsManager.isSoundEnabled = value
        }

    var isHapticsEnabled: Boolean
        get() = soundHapticsManager.isHapticsEnabled
        set(value) {
            soundHapticsManager.isHapticsEnabled = value
        }

    fun playClickSound() = soundHapticsManager.playClickSound()

    fun playRollSound() = soundHapticsManager.playRollSound()

    fun playCriticalHitSound() = soundHapticsManager.playCriticalHitSound()

    fun playCriticalFailSound() = soundHapticsManager.playCriticalFailSound()

    fun vibrateClick() = soundHapticsManager.vibrateClick()

    fun vibrateRoll() = soundHapticsManager.vibrateRoll()

    fun vibrateCriticalHit() = soundHapticsManager.vibrateCriticalHit()

    fun vibrateCriticalFail() = soundHapticsManager.vibrateCriticalFail()

    fun playClickFeedback() = soundHapticsManager.playClickFeedback()

    fun playRollFeedback() = soundHapticsManager.playRollFeedback()

    fun playCriticalHitFeedback() = soundHapticsManager.playCriticalHitFeedback()

    fun playCriticalFailFeedback() = soundHapticsManager.playCriticalFailFeedback()

    fun release() = soundHapticsManager.release()
}
