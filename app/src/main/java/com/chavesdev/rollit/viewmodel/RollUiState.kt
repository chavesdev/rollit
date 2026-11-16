package com.chavesdev.rollit.viewmodel

import com.chavesdev.rollit.model.DiceRollHistoryItem
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult

/**
 * UI State for the main dice rolling screen.
 */
data class RollUiState(
    val selectedDiceType: DiceType = DiceType.D6,
    val quantity: Int = 1,
    val modifier: Int = 0,
    val isRolling: Boolean = false,
    val rollingDisplayValues: List<Int> = emptyList(),
    val currentResult: RollResult? = null,
    val history: List<DiceRollHistoryItem> = emptyList(),
    val isShakeEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val shakeSensitivity: Float = 2.5f,
    val lastTrigger: RollTrigger? = null,
)
