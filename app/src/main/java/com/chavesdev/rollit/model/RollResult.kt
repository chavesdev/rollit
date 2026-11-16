package com.chavesdev.rollit.model

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.random.Random

/**
 * Represents the result of a single dice roll action (one or multiple dice + modifier).
 */
@Serializable
data class RollResult(
    val id: String = UUID.randomUUID().toString(),
    val diceType: DiceType,
    val quantity: Int = 1,
    val rolls: List<Int>,
    val modifier: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val label: String? = null,
) {
    val rawTotal: Int
        get() = rolls.sum()

    val total: Int
        get() = rawTotal + modifier

    val isNat20: Boolean
        get() = (diceType == DiceType.D20) && (quantity == 1) && (rolls.firstOrNull() == 20)

    val isNat1: Boolean
        get() = (diceType == DiceType.D20) && (quantity == 1) && (rolls.firstOrNull() == 1)

    val isCriticalHit: Boolean
        get() = isNat20 || (rolls.isNotEmpty() && rolls.all { it == diceType.sides })

    val isCriticalFail: Boolean
        get() = isNat1 || (rolls.isNotEmpty() && rolls.all { it == 1 })

    val formula: String
        get() {
            val base = "$quantity${diceType.displayName}"
            return when {
                modifier > 0 -> "$base+$modifier"
                modifier < 0 -> "$base$modifier"
                else -> base
            }
        }

    val formattedRolls: String
        get() = rolls.joinToString(prefix = "[", postfix = "]")

    companion object {
        fun create(
            diceType: DiceType,
            quantity: Int = 1,
            modifier: Int = 0,
            label: String? = null,
            random: Random = Random.Default,
        ): RollResult {
            val rollValues = diceType.rollMultiple(quantity, random)
            return RollResult(
                diceType = diceType,
                quantity = quantity,
                rolls = rollValues,
                modifier = modifier,
                timestamp = System.currentTimeMillis(),
                label = label,
            )
        }
    }
}
