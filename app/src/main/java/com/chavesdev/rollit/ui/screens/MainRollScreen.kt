package com.chavesdev.rollit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chavesdev.rollit.model.DiceRollHistoryItem
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult
import com.chavesdev.rollit.ui.components.AnimatedDiceView
import com.chavesdev.rollit.ui.components.SettingsSheet
import com.chavesdev.rollit.ui.components.ShakeSensorEffect
import com.chavesdev.rollit.ui.theme.DeepNavyBackground
import com.chavesdev.rollit.ui.theme.RollitTheme
import com.chavesdev.rollit.viewmodel.RollTrigger
import com.chavesdev.rollit.viewmodel.RollUiState
import com.chavesdev.rollit.viewmodel.RollViewModel

/**
 * Main Roll Screen composable bound to [RollViewModel].
 */
@Composable
fun MainRollScreen(
    viewModel: RollViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainRollScreenContent(
        uiState = uiState,
        onSelectDiceType = viewModel::selectDiceType,
        onQuantityChange = viewModel::setQuantity,
        onModifierChange = viewModel::setModifier,
        onRollClick = { viewModel.triggerRoll(RollTrigger.TAP_BUTTON) },
        onSwipeRoll = { viewModel.triggerRoll(RollTrigger.GESTURE_SWIPE) },
        onShakeRoll = { viewModel.rollDice(RollTrigger.DEVICE_SHAKE) },
        onQuickPresetClick = { diceType, quantity, modifier, label ->
            viewModel.triggerRoll(
                trigger = RollTrigger.QUICK_ACTION,
                customDiceType = diceType,
                customQuantity = quantity,
                customModifier = modifier,
                label = label
            )
        },
        onShakeToggle = viewModel::setShakeEnabled,
        onSoundToggle = viewModel::setSoundEnabled,
        onHapticsToggle = viewModel::setHapticsEnabled,
        onSensitivityChange = viewModel::setShakeSensitivity,
        modifier = modifier
    )
}

/**
 * Main Roll Screen stateless UI content for previewing and modularity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRollScreenContent(
    uiState: RollUiState,
    onSelectDiceType: (DiceType) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onModifierChange: (Int) -> Unit,
    onRollClick: () -> Unit,
    onSwipeRoll: () -> Unit,
    onShakeRoll: () -> Unit,
    onQuickPresetClick: (DiceType, Int, Int, String) -> Unit,
    onShakeToggle: (Boolean) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onSensitivityChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Sensor lifecycle integration for device shake gestures
    ShakeSensorEffect(
        isShakeEnabled = uiState.isShakeEnabled,
        sensitivity = uiState.shakeSensitivity,
        onShake = onShakeRoll
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rollit",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF050B14), DeepNavyBackground)
                ))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            // Configuration & Control Panel
//            DiceConfigPanel(
//                selectedDiceType = uiState.selectedDiceType,
//                quantity = uiState.quantity,
//                modifierValue = uiState.modifier,
//                isRolling = uiState.isRolling,
//                onDiceTypeSelected = onSelectDiceType,
//                onQuantityChange = onQuantityChange,
//                onModifierChange = onModifierChange,
//                onRollClick = onRollClick,
//                onClearClick = {
//                    onQuantityChange(1)
//                    onModifierChange(0)
//                },
//                onQuickPresetClick = onQuickPresetClick
//            )
//
            Spacer(modifier = Modifier.height(40.dp))


            // Animated Dice View Box
            AnimatedDiceView(
                diceType = uiState.selectedDiceType,
                quantity = uiState.quantity,
                isRolling = uiState.isRolling,
                rollingValues = uiState.rollingDisplayValues,
                currentResult = uiState.currentResult,
                onSwipeRoll = onSwipeRoll
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Roll History Component List
//            RollHistoryList(
//                history = uiState.history,
//                onToggleFavorite = onToggleFavorite,
//                onRepeatRoll = onRepeatRoll,
//                onClearHistory = onClearHistory
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
        }

        // Settings Bottom Sheet
        if (showSettingsSheet) {
            SettingsSheet(
                isShakeEnabled = uiState.isShakeEnabled,
                isSoundEnabled = uiState.isSoundEnabled,
                isHapticsEnabled = uiState.isHapticsEnabled,
                shakeSensitivity = uiState.shakeSensitivity,
                onShakeToggle = onShakeToggle,
                onSoundToggle = onSoundToggle,
                onHapticsToggle = onHapticsToggle,
                onSensitivityChange = onSensitivityChange,
                onDismissRequest = { showSettingsSheet = false }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainRollScreenPreview() {
    val sampleRoll = RollResult(
        diceType = DiceType.D6,
        quantity = 2,
        rolls = listOf(5, 6),
        modifier = 2,
        label = "Dmg Roll"
    )
    val sampleState = RollUiState(
        selectedDiceType = DiceType.D6,
        quantity = 2,
        modifier = 2,
        currentResult = sampleRoll,
        history = listOf(
            DiceRollHistoryItem(
                rollResult = sampleRoll,
                presetName = "Dmg Roll",
                isFavorite = true
            )
        )
    )

    RollitTheme {
        MainRollScreenContent(
            uiState = sampleState,
            onSelectDiceType = {},
            onQuantityChange = {},
            onModifierChange = {},
            onRollClick = {},
            onSwipeRoll = {},
            onShakeRoll = {},
            onQuickPresetClick = { _, _, _, _ -> },
            onShakeToggle = {},
            onSoundToggle = {},
            onHapticsToggle = {},
            onSensitivityChange = {}
        )
    }
}
