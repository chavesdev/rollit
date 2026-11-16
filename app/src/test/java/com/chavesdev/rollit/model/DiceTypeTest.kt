package com.chavesdev.rollit.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class DiceTypeTest {

    @Test
    fun testDiceSidesAndNames() {
        assertEquals(4, DiceType.D4.sides)
        assertEquals(6, DiceType.D6.sides)
        assertEquals(8, DiceType.D8.sides)
        assertEquals(10, DiceType.D10.sides)
        assertEquals(12, DiceType.D12.sides)
        assertEquals(20, DiceType.D20.sides)
        assertEquals(100, DiceType.D100.sides)

        assertEquals("D20", DiceType.D20.displayName)
    }

    @Test
    fun testRollWithinRange() {
        val random = Random(12345)
        for (dice in DiceType.entries) {
            val roll = dice.roll(random)
            assertTrue("Roll $roll should be >= 1", roll >= 1)
            assertTrue("Roll $roll should be <= ${dice.sides}", roll <= dice.sides)
        }
    }

    @Test
    fun testRollMultiple() {
        val random = Random(42)
        val rolls = DiceType.D6.rollMultiple(5, random)
        assertEquals(5, rolls.size)
        rolls.forEach { roll ->
            assertTrue(roll in 1..6)
        }
    }

    @Test
    fun testFromSides() {
        assertEquals(DiceType.D20, DiceType.fromSides(20))
        assertEquals(DiceType.D6, DiceType.fromSides(6))
        assertNull(DiceType.fromSides(7))
    }
}
