package com.chavesdev.rollit.viewmodel

import com.chavesdev.rollit.model.DiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class RollViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fixedRandom = Random(12345)
    private lateinit var viewModel: RollViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RollViewModel(random = fixedRandom)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultValues() {
        val state = viewModel.uiState.value
        assertEquals(DiceType.D6, state.selectedDiceType)
        assertEquals(1, state.quantity)
        assertEquals(0, state.modifier)
        assertFalse(state.isRolling)
        assertTrue(state.rollingDisplayValues.isEmpty())
        assertNull(state.currentResult)
        assertTrue(state.history.isEmpty())
        assertTrue(state.isShakeEnabled)
        assertTrue(state.isSoundEnabled)
        assertTrue(state.isHapticsEnabled)
        assertEquals(2.5f, state.shakeSensitivity, 0.001f)
        assertNull(state.lastTrigger)
    }

    @Test
    fun selectDiceType_updatesState() {
        viewModel.selectDiceType(DiceType.D20)
        assertEquals(DiceType.D20, viewModel.uiState.value.selectedDiceType)
    }

    @Test
    fun setQuantity_clampedWithinRange() {
        viewModel.setQuantity(5)
        assertEquals(5, viewModel.uiState.value.quantity)

        viewModel.setQuantity(15)
        assertEquals(10, viewModel.uiState.value.quantity)

        viewModel.setQuantity(0)
        assertEquals(1, viewModel.uiState.value.quantity)
    }

    @Test
    fun setModifier_clampedWithinRange() {
        viewModel.setModifier(3)
        assertEquals(3, viewModel.uiState.value.modifier)

        viewModel.setModifier(20)
        assertEquals(10, viewModel.uiState.value.modifier)

        viewModel.setModifier(-15)
        assertEquals(-10, viewModel.uiState.value.modifier)
    }

    @Test
    fun settingToggles_updateState() {
        viewModel.setShakeEnabled(false)
        assertFalse(viewModel.uiState.value.isShakeEnabled)

        viewModel.setSoundEnabled(false)
        assertFalse(viewModel.uiState.value.isSoundEnabled)

        viewModel.setHapticsEnabled(false)
        assertFalse(viewModel.uiState.value.isHapticsEnabled)

        viewModel.setShakeSensitivity(4.0f)
        assertEquals(4.0f, viewModel.uiState.value.shakeSensitivity, 0.001f)
    }

    @Test
    fun rollDice_executesRollingAnimationAndUpdatesResultAndHistory() = runTest {
        viewModel.selectDiceType(DiceType.D6)
        viewModel.setQuantity(2)
        viewModel.setModifier(1)

        viewModel.rollDice(trigger = RollTrigger.TAP_BUTTON)

        // During execution before finishing, rolling state should be active
        assertTrue(viewModel.uiState.value.isRolling)
        assertEquals(RollTrigger.TAP_BUTTON, viewModel.uiState.value.lastTrigger)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRolling)
        assertTrue(state.rollingDisplayValues.isEmpty())

        val result = state.currentResult
        assertNotNull(result)
        assertEquals(DiceType.D6, result?.diceType)
        assertEquals(2, result?.quantity)
        assertEquals(1, result?.modifier)

        assertEquals(1, state.history.size)
        assertEquals(result, state.history.first().rollResult)
    }

    @Test
    fun triggerRoll_withCustomParameters_usesCustomParams() = runTest {
        viewModel.triggerRoll(
            trigger = RollTrigger.GESTURE_SWIPE,
            customDiceType = DiceType.D100,
            customQuantity = 3,
            customModifier = -2,
            label = "Percentile Check"
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(RollTrigger.GESTURE_SWIPE, state.lastTrigger)

        val result = state.currentResult
        assertNotNull(result)
        assertEquals(DiceType.D100, result?.diceType)
        assertEquals(3, result?.quantity)
        assertEquals(-2, result?.modifier)
        assertEquals("Percentile Check", result?.label)
    }

    @Test
    fun toggleFavorite_togglesItemFavoriteStatus() = runTest {
        viewModel.rollDice()
        advanceUntilIdle()

        val historyItem = viewModel.uiState.value.history.first()
        assertFalse(historyItem.isFavorite)

        viewModel.toggleFavorite(historyItem.id)
        assertTrue(viewModel.uiState.value.history.first().isFavorite)

        viewModel.toggleFavorite(historyItem.id)
        assertFalse(viewModel.uiState.value.history.first().isFavorite)
    }

    @Test
    fun repeatRoll_usesHistoryItemParameters() = runTest {
        viewModel.selectDiceType(DiceType.D20)
        viewModel.setQuantity(2)
        viewModel.setModifier(4)
        viewModel.rollDice(label = "Attack Roll")
        advanceUntilIdle()

        val firstItem = viewModel.uiState.value.history.first()

        // Change current settings
        viewModel.selectDiceType(DiceType.D4)
        viewModel.setQuantity(1)
        viewModel.setModifier(0)

        // Repeat first roll
        viewModel.repeatRoll(firstItem.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(DiceType.D20, state.selectedDiceType)
        assertEquals(2, state.quantity)
        assertEquals(4, state.modifier)
        assertEquals(2, state.history.size)
        assertEquals(RollTrigger.QUICK_ACTION, state.lastTrigger)
    }

    @Test
    fun rollDice_ignoredIfAlreadyRolling() = runTest {
        viewModel.rollDice()
        assertTrue(viewModel.uiState.value.isRolling)

        // Attempt a second roll while first is in progress
        viewModel.rollDice()

        advanceUntilIdle()

        // History should only have 1 item from the single execution
        assertEquals(1, viewModel.uiState.value.history.size)
    }
}
