package com.chavesdev.rollit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chavesdev.rollit.R
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.ui.theme.RollitTheme
import com.chavesdev.rollit.viewmodel.RollViewModel

/**
 * Configuration & Control Panel for selecting dice types, adjusting quantity/modifiers,
 * triggering rolls, clearing state, and picking quick presets.
 */
@Composable
fun DiceConfigPanel(
    selectedDiceType: DiceType,
    quantity: Int,
    modifierValue: Int,
    isRolling: Boolean,
    onDiceTypeSelected: (DiceType) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onModifierChange: (Int) -> Unit,
    onRollClick: () -> Unit,
    onClearClick: () -> Unit,
    onQuickPresetClick: (DiceType, Int, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CounterControl(
                label = stringResource(R.string.quantity),
                value = quantity,
                minValue = RollViewModel.MIN_QUANTITY,
                maxValue = RollViewModel.MAX_QUANTITY,
                onValueChange = onQuantityChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Reusable stepper counter control (+ / - buttons with numeric text display).
 */
@Composable
private fun CounterControl(
    label: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    formatValue: (Int) -> String = { it.toString() },
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val decreaseDesc = stringResource(R.string.decrease_format, label)
                IconButton(
                    onClick = { onValueChange((value - 1).coerceAtLeast(minValue)) },
                    enabled = value > minValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = decreaseDesc,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = formatValue(value),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                val increaseDesc = stringResource(R.string.increase_format, label)
                IconButton(
                    onClick = { onValueChange((value + 1).coerceAtMost(maxValue)) },
                    enabled = value < maxValue,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = increaseDesc,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickPresetChip(
    name: String,
    onClick: () -> Unit,
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DiceConfigPanelPreview() {
    RollitTheme {
        DiceConfigPanel(
            selectedDiceType = DiceType.D6,
            quantity = 2,
            modifierValue = 3,
            isRolling = false,
            onDiceTypeSelected = {},
            onQuantityChange = {},
            onModifierChange = {},
            onRollClick = {},
            onClearClick = {},
            onQuickPresetClick = { _, _, _, _ -> }
        )
    }
}
