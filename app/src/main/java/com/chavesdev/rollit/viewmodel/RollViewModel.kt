package com.chavesdev.rollit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chavesdev.rollit.hardware.FeedbackManager
import com.chavesdev.rollit.model.DiceRollHistoryItem
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel responsible for managing dice selection, modifier/quantity state,
 * rolling animation state, user settings, and history tracking.
 */
class RollViewModel(
    private val feedbackManager: FeedbackManager? = null,
    private val random: Random = Random.Default,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RollUiState())
    val uiState: StateFlow<RollUiState> = _uiState.asStateFlow()

    private var rollJob: Job? = null

    init {
        feedbackManager?.let {
            it.isSoundEnabled = _uiState.value.isSoundEnabled
            it.isHapticsEnabled = _uiState.value.isHapticsEnabled
        }
    }

    fun selectDiceType(diceType: DiceType) {
        _uiState.update { it.copy(selectedDiceType = diceType) }
    }

    fun setQuantity(quantity: Int) {
        val clamped = quantity.coerceIn(MIN_QUANTITY, MAX_QUANTITY)
        _uiState.update { it.copy(quantity = clamped) }
    }

    fun setModifier(modifier: Int) {
        val clamped = modifier.coerceIn(MIN_MODIFIER, MAX_MODIFIER)
        _uiState.update { it.copy(modifier = clamped) }
    }

    fun setShakeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isShakeEnabled = enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isSoundEnabled = enabled) }
        feedbackManager?.isSoundEnabled = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isHapticsEnabled = enabled) }
        feedbackManager?.isHapticsEnabled = enabled
    }

    fun setShakeSensitivity(sensitivity: Float) {
        _uiState.update { it.copy(shakeSensitivity = sensitivity) }
    }

    fun setUpdateAvailable(available: Boolean) {
        _uiState.update { it.copy(isUpdateAvailable = available) }
    }

    fun triggerRoll(
        trigger: RollTrigger = RollTrigger.GESTURE_SWIPE,
        customDiceType: DiceType? = null,
        customQuantity: Int? = null,
        customModifier: Int? = null,
        label: String? = null,
    ) {
        rollDice(
            trigger = trigger,
            diceType = customDiceType,
            quantity = customQuantity,
            modifier = customModifier,
            label = label,
        )
    }

    fun resetRoll() {
        rollJob?.cancel()
        _uiState.update {
            it.copy(
                isRolling = false,
                rollingDisplayValues = emptyList(),
                currentResult = null
            )
        }
    }

    fun rollDice(
        trigger: RollTrigger = RollTrigger.GESTURE_SWIPE,
        diceType: DiceType? = null,
        quantity: Int? = null,
        modifier: Int? = null,
        label: String? = null,
    ) {
        if (_uiState.value.isRolling) return

        val targetDiceType = diceType ?: _uiState.value.selectedDiceType
        val targetQuantity = (quantity ?: _uiState.value.quantity)
        val targetModifier = (modifier ?: _uiState.value.modifier)

        rollJob?.cancel()
        rollJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRolling = true,
                    lastTrigger = trigger
                )
            }

            // Coroutine-based rolling animation sequence over ~600ms
            val steps = ANIMATION_STEPS
            val stepDelay = ANIMATION_DURATION_MS / steps

            for (step in 1..steps) {
                val displayValues = List(targetQuantity) { random.nextInt(1, targetDiceType.sides + 1) }
                _uiState.update { it.copy(rollingDisplayValues = displayValues) }

                if (_uiState.value.isSoundEnabled || _uiState.value.isHapticsEnabled) {
                    feedbackManager?.playClickFeedback()
                }

                delay(stepDelay)
            }

            // Settle on final result
            val rollValues = targetDiceType.rollMultiple(targetQuantity, random)
            val finalResult = RollResult(
                diceType = targetDiceType,
                quantity = targetQuantity,
                rolls = rollValues,
                modifier = targetModifier,
                label = label,
            )

            _uiState.update { state ->
                state.copy(
                    isRolling = false,
                    rollingDisplayValues = emptyList(),
                    currentResult = finalResult,
                )
            }

            // Feedback on final result
            if (finalResult.isCriticalHit) {
                feedbackManager?.playCriticalHitFeedback()
            } else if (finalResult.isCriticalFail) {
                feedbackManager?.playCriticalFailFeedback()
            } else {
                feedbackManager?.playRollFeedback()
            }
        }
    }

    fun toggleFavorite(historyItemId: String) {
        _uiState.update { state ->
            val updatedHistory = state.history.map { item ->
                if (item.id == historyItemId) {
                    item.copy(isFavorite = !item.isFavorite)
                } else {
                    item
                }
            }
            state.copy(history = updatedHistory)
        }
    }

    fun repeatRoll(historyItem: DiceRollHistoryItem) {
        selectDiceType(historyItem.rollResult.diceType)
        setQuantity(historyItem.rollResult.quantity)
        setModifier(historyItem.rollResult.modifier)
        triggerRoll(
            trigger = RollTrigger.GESTURE_SWIPE,
            customDiceType = historyItem.rollResult.diceType,
            customQuantity = historyItem.rollResult.quantity,
            customModifier = historyItem.rollResult.modifier,
            label = historyItem.presetName ?: historyItem.rollResult.label,
        )
    }

    fun repeatRoll(historyItemId: String) {
        val item = _uiState.value.history.firstOrNull { it.id == historyItemId }
        if (item != null) {
            repeatRoll(item)
        }
    }

    companion object {
        const val MIN_QUANTITY = 1
        const val MAX_QUANTITY = 10
        const val MIN_MODIFIER = -10
        const val MAX_MODIFIER = 10
        const val ANIMATION_DURATION_MS = 600L
        const val ANIMATION_STEPS = 6
    }
}
