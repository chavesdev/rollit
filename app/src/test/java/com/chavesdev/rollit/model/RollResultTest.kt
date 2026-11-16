package com.chavesdev.rollit.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RollResultTest {

    @Test
    fun testTotalCalculation() {
        val result = RollResult(
            diceType = DiceType.D6,
            quantity = 3,
            rolls = listOf(3, 4, 5),
            modifier = 2
        )

        assertEquals(12, result.rawTotal)
        assertEquals(14, result.total)
        assertEquals("3D6+2", result.formula)
        assertEquals("[3, 4, 5]", result.formattedRolls)
    }

    @Test
    fun testNegativeModifierFormula() {
        val result = RollResult(
            diceType = DiceType.D20,
            quantity = 1,
            rolls = listOf(15),
            modifier = -1
        )

        assertEquals(14, result.total)
        assertEquals("1D20-1", result.formula)
    }

    @Test
    fun testNat20AndNat1() {
        val nat20 = RollResult(
            diceType = DiceType.D20,
            quantity = 1,
            rolls = listOf(20)
        )
        assertTrue(nat20.isNat20)
        assertFalse(nat20.isNat1)
        assertTrue(nat20.isCriticalHit)
        assertFalse(nat20.isCriticalFail)

        val nat1 = RollResult(
            diceType = DiceType.D20,
            quantity = 1,
            rolls = listOf(1)
        )
        assertFalse(nat1.isNat20)
        assertTrue(nat1.isNat1)
        assertFalse(nat1.isCriticalHit)
        assertTrue(nat1.isCriticalFail)
    }

    @Test
    fun testFactoryCreate() {
        val random = Random(99)
        val result = RollResult.create(
            diceType = DiceType.D8,
            quantity = 2,
            modifier = 3,
            label = "Longsword Damage",
            random = random
        )

        assertEquals(DiceType.D8, result.diceType)
        assertEquals(2, result.quantity)
        assertEquals(2, result.rolls.size)
        assertEquals(3, result.modifier)
        assertEquals("Longsword Damage", result.label)
        assertEquals(result.rolls.sum() + 3, result.total)
    }
}
