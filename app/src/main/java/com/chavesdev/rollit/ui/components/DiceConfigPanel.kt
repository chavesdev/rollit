package com.chavesdev.rollit.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chavesdev.rollit.model.DiceType
import com.chavesdev.rollit.ui.theme.RollitTheme
import com.chavesdev.rollit.viewmodel.RollViewModel

/**
 * Configuration & Control Panel for selecting dice types, adjusting quantity/modifiers,
 * triggering rolls, clearing state, and picking quick presets.
 */
@OptIn(ExperimentalLayoutApi::class)
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
//                .padding(16.dp)
        ) {

            CounterControl(
                label = "Quantity",
                value = quantity,
                minValue = RollViewModel.MIN_QUANTITY,
                maxValue = RollViewModel.MAX_QUANTITY,
                onValueChange = onQuantityChange,
                modifier = Modifier.weight(1f)
            )
            // Dice Type Selector Chips Row
//            Text(
//                text = "Select Dice",
//                style = MaterialTheme.typography.labelMedium.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.primary
//                )
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(DiceType.standardDice) { diceType ->
//                    val selected = (diceType == selectedDiceType)
//                    FilterChip(
//                        selected = selected,
//                        onClick = { onDiceTypeSelected(diceType) },
//                        label = {
//                            Text(
//                                text = diceType.displayName,
//                                style = MaterialTheme.typography.labelLarge.copy(
//                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
//                                )
//                            )
//                        },
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = MaterialTheme.colorScheme.primary,
//                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
//                        )
//                    )
//                }
//            }

//            Spacer(modifier = Modifier.height(16.dp))

            // Counters Row: Quantity & Modifier
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Quantity Counter
////                CounterControl(
////                    label = "Quantity",
////                    value = quantity,
////                    minValue = RollViewModel.MIN_QUANTITY,
////                    maxValue = RollViewModel.MAX_QUANTITY,
////                    onValueChange = onQuantityChange,
////                    modifier = Modifier.weight(1f)
////                )
//
////                Spacer(modifier = Modifier.width(16.dp))
////
////                // Modifier Counter
////                CounterControl(
////                    label = "Modifier",
////                    value = modifierValue,
////                    minValue = RollViewModel.MIN_MODIFIER,
////                    maxValue = RollViewModel.MAX_MODIFIER,
////                    formatValue = { if (it > 0) "+$it" else "$it" },
////                    onValueChange = onModifierChange,
////                    modifier = Modifier.weight(1f)
////                )
//            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Roll Dice & Clear
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Button(
//                    onClick = onRollClick,
//                    enabled = !isRolling,
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(52.dp),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Casino,
//                        contentDescription = "Roll Dice",
//                        modifier = Modifier.size(24.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    val modStr = when {
//                        modifierValue > 0 -> "+$modifierValue"
//                        modifierValue < 0 -> "$modifierValue"
//                        else -> ""
//                    }
//                    Text(
//                        text = "Roll ${quantity}${selectedDiceType.displayName}$modStr",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
//                    )
//                }
//
//                OutlinedButton(
//                    onClick = onClearClick,
//                    modifier = Modifier.height(52.dp),
//                    shape = RoundedCornerShape(16.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Clear,
//                        contentDescription = "Reset",
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

            // Quick Presets Row
//            Text(
//                text = "Quick Presets",
//                style = MaterialTheme.typography.labelMedium.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .horizontalScroll(rememberScrollState()),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                QuickPresetChip(
//                    name = "Advantage (2D20)",
//                    onClick = { onQuickPresetClick(DiceType.D20, 2, 0, "Advantage") }
//                )
//                QuickPresetChip(
//                    name = "Disadvantage (2D20)",
//                    onClick = { onQuickPresetClick(DiceType.D20, 2, 0, "Disadvantage") }
//                )
//                QuickPresetChip(
//                    name = "Fireball (8D6)",
//                    onClick = { onQuickPresetClick(DiceType.D6, 8, 0, "Fireball") }
//                )
//                QuickPresetChip(
//                    name = "Cure Wounds (2D8+2)",
//                    onClick = { onQuickPresetClick(DiceType.D8, 2, 2, "Cure Wounds") }
//                )
//                QuickPresetChip(
//                    name = "Stat Check (4D6)",
//                    onClick = { onQuickPresetClick(DiceType.D6, 4, 0, "Stat Check") }
//                )
//            }
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
                        contentDescription = "Decrease $label",
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
                        contentDescription = "Increase $label",
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
