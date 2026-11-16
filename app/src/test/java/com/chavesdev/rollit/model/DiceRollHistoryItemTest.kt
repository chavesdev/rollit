package com.chavesdev.rollit.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DiceRollHistoryItemTest {

    @Test
    fun testHistoryItemSummaryWithPresetName() {
        val rollResult = RollResult(
            diceType = DiceType.D6,
            quantity = 2,
            rolls = listOf(4, 5),
            modifier = 1
        )
        val historyItem = DiceRollHistoryItem(
            rollResult = rollResult,
            presetName = "Fireball Damage"
        )

        assertEquals("Fireball Damage = 10 [4, 5]", historyItem.summary)
        assertEquals(rollResult.timestamp, historyItem.timestamp)
    }

    @Test
    fun testHistoryItemSummaryWithoutPreset() {
        val rollResult = RollResult(
            diceType = DiceType.D20,
            quantity = 1,
            rolls = listOf(18),
            modifier = 5,
            label = "Attack Roll"
        )
        val historyItem = DiceRollHistoryItem(rollResult = rollResult)

        assertEquals("Attack Roll = 23 [18]", historyItem.summary)
    }
}
