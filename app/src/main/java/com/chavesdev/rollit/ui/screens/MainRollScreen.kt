package com.chavesdev.rollit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chavesdev.rollit.R
import com.chavesdev.rollit.model.DiceRollHistoryItem
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.model.RollResult
import com.chavesdev.rollit.ui.components.AnimatedDiceView
import com.chavesdev.rollit.ui.components.SettingsSheet
import com.chavesdev.rollit.ui.components.ShakeSensorEffect
import com.chavesdev.rollit.ui.components.UpdateAvailableSheet
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
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainRollScreenContent(
        uiState = uiState,
        onSwipeRoll = { viewModel.triggerRoll(RollTrigger.GESTURE_SWIPE) },
        onShakeRoll = { viewModel.rollDice(RollTrigger.DEVICE_SHAKE) },
        onShakeToggle = viewModel::setShakeEnabled,
        onSoundToggle = viewModel::setSoundEnabled,
        onHapticsToggle = viewModel::setHapticsEnabled,
        onSensitivityChange = viewModel::setShakeSensitivity,
        onUpdateClick = onUpdateClick,
        onUpdateDismiss = { viewModel.setUpdateAvailable(false) },
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
    onSwipeRoll: () -> Unit,
    onShakeRoll: () -> Unit,
    onShakeToggle: (Boolean) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit,
    onSensitivityChange: (Float) -> Unit,
    onUpdateClick: () -> Unit,
    onUpdateDismiss: () -> Unit,
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
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF0F172A), DeepNavyBackground),
                        radius = 1200f
                    )
                )
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Immersive Fullscreen Animated Dice View
            AnimatedDiceView(
                diceType = uiState.selectedDiceType,
                quantity = uiState.quantity,
                isRolling = uiState.isRolling,
                rollingValues = uiState.rollingDisplayValues,
                currentResult = uiState.currentResult,
                onSwipeRoll = onSwipeRoll,
                modifier = Modifier.fillMaxSize()
            )
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

        // Update Available Bottom Sheet
        if (uiState.isUpdateAvailable) {
            UpdateAvailableSheet(
                onUpdateClick = onUpdateClick,
                onDismissRequest = onUpdateDismiss
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainRollScreenPreview() {
    val sampleRoll = RollResult(
        diceType = DiceType.D20,
        quantity = 1,
        rolls = listOf(20),
        modifier = 3,
        label = "Attack Roll"
    )
    val sampleState = RollUiState(
        selectedDiceType = DiceType.D20,
        quantity = 1,
        modifier = 3,
        currentResult = sampleRoll,
        history = listOf(
            DiceRollHistoryItem(
                rollResult = sampleRoll,
                presetName = "Attack Roll",
                isFavorite = true
            )
        )
    )

    RollitTheme {
        MainRollScreenContent(
            uiState = sampleState,
            onSwipeRoll = {},
            onShakeRoll = {},
            onShakeToggle = {},
            onSoundToggle = {},
            onHapticsToggle = {},
            onSensitivityChange = {},
            onUpdateClick = {},
            onUpdateDismiss = {}
        )
    }
}
