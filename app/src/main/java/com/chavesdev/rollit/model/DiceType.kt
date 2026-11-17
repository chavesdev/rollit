package com.chavesdev.rollit.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Standard dice types supported by the Rollit dice roller application.
 * At the moment I'm just using a die with 6 sides
 */
@Serializable
enum class DiceType(
    val sides: Int,
    val displayName: String,
    val description: String,
) {
    D4(4, "D4", "4-sided die"),
    D6(6, "D6", "6-sided die"),
    D8(8, "D8", "8-sided die"),
    D10(10, "D10", "10-sided die"),
    D12(12, "D12", "12-sided die"),
    D20(20, "D20", "20-sided die"),
    D100(100, "D100", "100-sided percentile die");

    val maxRoll: Int
        get() = sides

    /**
     * Rolls this die once returning a random value between 1 and [sides].
     */
    fun roll(random: Random = Random.Default): Int {
        return random.nextInt(1, sides + 1)
    }

    /**
     * Rolls this die [count] times returning a list of individual roll results.
     */
    fun rollMultiple(count: Int, random: Random = Random.Default): List<Int> {
        require(count > 0) { "Roll count must be at least 1" }
        return List(count) { roll(random) }
    }

    companion object {
        fun fromSides(sides: Int): DiceType? {
            return entries.firstOrNull { it.sides == sides }
        }
    }
}
