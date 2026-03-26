package com.mena97villalobos.designsystem.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.core.TextColor
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.designsystem.theme.typography
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens

@Composable
fun TextInputCard(
    title: String,
    value: String,
    textFieldStyle: TextStyle,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    hint: String? = null,
    enabled: Boolean = true,
    testTag: String = "CardInput",
    disabledTextColor: TextColor = TextColor.BLACK40,
    hintAndUnitTextColor: TextColor = TextColor.WARMGREY500,
) {
    val cardShape = RoundedCornerShape(DesignSystemDimens.Radius20)
    val internalHintAndUnitTextColor: TextColor =
        if (enabled) hintAndUnitTextColor else disabledTextColor

    TextInputCardLayout(
        modifier = modifier,
        cardShape = cardShape,
        title = title,
        testTag = testTag,
        textInput = {
            TextInputCardTextField(
                value = value,
                onValueChange = onValueChange,
                textFieldStyle = textFieldStyle,
                enabled = enabled,
                maxLines = maxLines,
                hint = hint,
                hintColor = internalHintAndUnitTextColor,
                disabledTextColor = disabledTextColor,
            )
        },
    )
}

@Composable
fun TextInputCardWithDropDown(
    title: String,
    value: String,
    textFieldStyle: TextStyle,
    onValueChange: (String) -> Unit,
    dropdownOptions: List<String>,
    selectedDropdownOption: Int,
    onDropdownOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    hint: String? = null,
    enabled: Boolean = true,
    testTag: String = "CardInput",
    disabledTextColor: TextColor = TextColor.BLACK40,
    hintAndUnitTextColor: TextColor = TextColor.WARMGREY500,
) {
    val cardShape = RoundedCornerShape(DesignSystemDimens.Radius24)
    val internalHintAndUnitTextColor: TextColor =
        if (enabled) hintAndUnitTextColor else disabledTextColor

    TextInputCardLayout(
        modifier = modifier,
        cardShape = cardShape,
        title = title,
        testTag = testTag,
        textInput = {
            TextInputCardTextField(
                value = value,
                onValueChange = onValueChange,
                textFieldStyle = textFieldStyle,
                enabled = enabled,
                maxLines = maxLines,
                hint = hint,
                hintColor = internalHintAndUnitTextColor,
                disabledTextColor = disabledTextColor,
            )
        },
        trailingContent = {
            Dropdown(
                items = dropdownOptions,
                selectedIndex = selectedDropdownOption,
                onItemSelected = { onDropdownOptionSelected(dropdownOptions[it]) },
                dropdownStyle = DropdownStyle.SM,
                noSelectionText = "Select Value",
                enabled = enabled,
            )
        },
    )
}

@Composable
private fun TextInputCardLayout(
    modifier: Modifier,
    cardShape: RoundedCornerShape,
    title: String,
    testTag: String,
    textInput: @Composable () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        color = MaterialTheme.colorScheme.surface,
        shape = cardShape,
    ) {
        Column(
            modifier = Modifier.padding(DesignSystemDimens.Margin3x),
            verticalArrangement = Arrangement.spacedBy(DesignSystemDimens.Margin2x),
        ) {
            Text(
                text = title.uppercase(),
                style = typography.titleSmall,
                color = TextColor.WARMGREY500.color,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                textInput()
                trailingContent?.invoke()
            }
        }
    }
}

@Composable
private fun TextInputCardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textFieldStyle: TextStyle,
    enabled: Boolean,
    maxLines: Int,
    hint: String?,
    hintColor: TextColor,
    disabledTextColor: TextColor,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        textStyle = textFieldStyle.copy(
            color = if (enabled) TextColor.GREEN800.color else disabledTextColor.color,
        ),
        cursorBrush = SolidColor(if (enabled) TextColor.GREEN800.color else disabledTextColor.color),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box {
                if (!hint.isNullOrBlank() && value.isBlank()) {
                    Text(
                        text = hint,
                        color = hintColor.color,
                        style = textFieldStyle,
                        maxLines = maxLines,
                    )
                }
                innerTextField()
            }
        },
    )
}

private enum class DropdownStyle {
    SM,
}

@Composable
private fun Dropdown(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    dropdownStyle: DropdownStyle,
    noSelectionText: String,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedText = items.getOrNull(selectedIndex) ?: noSelectionText
    val canOpen = enabled && items.isNotEmpty()

    Row(
        modifier = Modifier
            .clickable(enabled = canOpen) { expanded = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DesignSystemDimens.MarginHalf),
    ) {
        Text(
            text = selectedText,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )

        IconButton(
            onClick = { if (canOpen) expanded = true },
            enabled = canOpen,
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Open dropdown",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                    expanded = false
                    onItemSelected(index)
                },
            )
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewTextInputCardEnabled() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextInputCard(
                title = "Name",
                value = "",
                onValueChange = {},
                hint = "Enter your name",
                textFieldStyle = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            TextInputCard(
                title = "Name",
                value = "John Doe",
                onValueChange = {},
                hint = "Enter your name",
                textFieldStyle = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewTextInputCardDisabled() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextInputCard(
                title = "Name",
                value = "",
                onValueChange = {},
                hint = "Disabled",
                enabled = false,
                textFieldStyle = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            TextInputCard(
                title = "Name",
                value = "John Doe",
                onValueChange = {},
                hint = "Disabled",
                enabled = false,
                textFieldStyle = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewTextInputCardWithDropDown() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextInputCardWithDropDown(
                title = "Amount",
                value = "",
                onValueChange = {},
                textFieldStyle = typography.bodyMedium,
                dropdownOptions = listOf("IU", "mg", "mcg"),
                selectedDropdownOption = -1,
                onDropdownOptionSelected = {},
                hint = "Type amount",
                modifier = Modifier.fillMaxWidth(),
            )

            TextInputCardWithDropDown(
                title = "Amount",
                value = "120",
                onValueChange = {},
                textFieldStyle = typography.bodyMedium,
                dropdownOptions = listOf("IU", "mg", "mcg"),
                selectedDropdownOption = 1,
                onDropdownOptionSelected = {},
                hint = "Type amount",
                modifier = Modifier.fillMaxWidth(),
            )

            TextInputCardWithDropDown(
                title = "Amount",
                value = "120",
                onValueChange = {},
                textFieldStyle = typography.bodyMedium,
                dropdownOptions = listOf("IU", "mg", "mcg"),
                selectedDropdownOption = 1,
                onDropdownOptionSelected = {},
                hint = "Disabled",
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
