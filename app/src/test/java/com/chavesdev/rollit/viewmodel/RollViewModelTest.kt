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
    fun setUpdateAvailable_updatesState() {
        assertFalse(viewModel.uiState.value.isUpdateAvailable)
        viewModel.setUpdateAvailable(true)
        assertTrue(viewModel.uiState.value.isUpdateAvailable)
        viewModel.setUpdateAvailable(false)
        assertFalse(viewModel.uiState.value.isUpdateAvailable)
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
    fun rollDice_completesAndSetsResult() = runTest {
        viewModel.rollDice()
        assertTrue(viewModel.uiState.value.isRolling)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRolling)
        assertNotNull(viewModel.uiState.value.currentResult)
    }

    @Test
    fun rollDice_ignoredIfAlreadyRolling() = runTest {
        viewModel.rollDice()
        assertTrue(viewModel.uiState.value.isRolling)

        // Attempt a second roll while first is in progress
        viewModel.rollDice()

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRolling)
        assertNotNull(viewModel.uiState.value.currentResult)
    }
}
