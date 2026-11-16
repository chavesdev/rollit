package com.chavesdev.rollit.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Historical record of a completed dice roll session or preset execution.
 */
@Serializable
data class DiceRollHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val rollResult: RollResult,
    val presetName: String? = null,
    val isFavorite: Boolean = false,
    val timestamp: Long = rollResult.timestamp,
    val note: String? = null,
) {
    val summary: String
        get() {
            val title = presetName ?: rollResult.label ?: rollResult.formula
            return "$title = ${rollResult.total} ${rollResult.formattedRolls}"
        }
}
