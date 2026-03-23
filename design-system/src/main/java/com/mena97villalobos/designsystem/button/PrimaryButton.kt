package com.mena97villalobos.designsystem.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.R
import com.mena97villalobos.designsystem.core.TextColor
import com.mena97villalobos.designsystem.core.conditional
import com.mena97villalobos.designsystem.theme.Black30
import com.mena97villalobos.designsystem.theme.Blue800
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.designsystem.theme.typography

enum class ButtonStyle {
    LARGE,
    MEDIUM,
    SMALL,
    X_SMALL,
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonStyle: ButtonStyle = ButtonStyle.LARGE,
    maxLines: Int = Int.MAX_VALUE,
    overrideColor: Color? = null,
    overrideTextColor: TextColor? = null,
    overrideDisabledColor: Color? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingColor: Color = Color.White,
    buttonIconInfo: ButtonIconInfo? = null,
    testTag: String = "PrimaryButton_$text",
) {
    Button(
        onClick = if (isLoading) {
            {}
        } else {
            onClick
        },
        enabled = enabled,
        shape = getButtonShape(buttonStyle),
        colors = getPrimaryButtonColors(
            overrideColor,
            overrideTextColor?.color,
            overrideDisabledColor,
        ),
        contentPadding = getButtonContentPadding(buttonStyle),
        modifier = modifier.then(Modifier.testTag(testTag)),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = loadingColor,
                modifier = Modifier.size(getButtonLoadingSize(buttonStyle)),
                strokeWidth = dimensionResource(R.dimen.button_progress_indicator_stroke_width),
            )
        } else {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (buttonIconInfo?.iconAlignment == IconAlignment.START) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = buttonIconInfo.iconResId),
                            tint = buttonIconInfo.iconColor ?: overrideTextColor?.color,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = dimensionResource(id = R.dimen.margin_half))
                                .size(getButtonIconSize(buttonStyle = buttonStyle)),
                        )
                    }
                    Text(
                        text = text,
                        style = getButtonTextStyle(buttonStyle),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .conditional(buttonIconInfo?.iconAlignment == IconAlignment.END) {
                                padding(
                                    end = getButtonIconSize(buttonStyle = buttonStyle) +
                                        dimensionResource(id = R.dimen.margin_half),
                                )
                            },
                        color = overrideTextColor?.color ?: TextColor.WHITE.color,
                        maxLines = maxLines,
                    )
                }
                if (buttonIconInfo?.iconAlignment == IconAlignment.END) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = buttonIconInfo.iconResId),
                        tint = buttonIconInfo.iconColor ?: overrideTextColor?.color ?: Color.White,
                        contentDescription = null,
                        modifier = Modifier
                            .size(getButtonIconSize(buttonStyle = buttonStyle))
                            .align(Alignment.CenterEnd),
                    )
                }
            }
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewPrimaryButton() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_2x)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(text = "Large", onClick = { }, modifier = Modifier.fillMaxWidth())
            PrimaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    iconResId = R.drawable.ic_android_preview,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.END,
                ),
            )
            PrimaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    iconResId = R.drawable.ic_android_preview,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.START,
                ),
            )
            PrimaryButton(text = "Medium", onClick = { }, buttonStyle = ButtonStyle.MEDIUM)
            PrimaryButton(text = "Small", onClick = { }, buttonStyle = ButtonStyle.SMALL)
            PrimaryButton(text = "X Small", onClick = { }, buttonStyle = ButtonStyle.X_SMALL)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonStyle: ButtonStyle = ButtonStyle.LARGE,
    enabled: Boolean = true,
    textColor: TextColor = TextColor.BLUE800,
    borderColor: Color = Blue800,
    buttonIconInfo: ButtonIconInfo? = null,
    testTag: String = "SecondaryButton_$text",
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        border = getSecondaryButtonBorderColors(buttonStyle, enabled, borderColor),
        colors = getSecondaryButtonColors(),
        shape = getButtonShape(buttonStyle),
        contentPadding = getButtonContentPadding(buttonStyle),
        modifier = modifier.then(Modifier.testTag(testTag)),
    ) {
        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (buttonIconInfo?.iconAlignment == IconAlignment.START) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = buttonIconInfo.iconResId),
                        tint = if (enabled) {
                            buttonIconInfo.iconColor ?: textColor.color
                        } else {
                            Black30
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = dimensionResource(id = R.dimen.margin_half))
                            .size(getButtonIconSize(buttonStyle = buttonStyle)),
                    )
                }
                Text(
                    text = text,
                    style = getButtonTextStyle(buttonStyle),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .conditional(buttonIconInfo?.iconAlignment == IconAlignment.END) {
                            padding(
                                end = getButtonIconSize(buttonStyle = buttonStyle) +
                                    dimensionResource(id = R.dimen.margin_half),
                            )
                        },
                    color = if (enabled) textColor.color else TextColor.BLACK40.color,
                )
            }
            if (buttonIconInfo?.iconAlignment == IconAlignment.END) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = buttonIconInfo.iconResId),
                    tint = if (enabled) {
                        buttonIconInfo.iconColor ?: textColor.color
                    } else {
                        Black30
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .size(getButtonIconSize(buttonStyle = buttonStyle))
                        .align(Alignment.CenterEnd),
                )
            }
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewSecondaryButton() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_2x)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SecondaryButton(text = "Large", onClick = { }, modifier = Modifier.fillMaxWidth())
            SecondaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    iconResId = R.drawable.ic_android_preview,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.END,
                ),
            )
            SecondaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    iconResId = R.drawable.ic_android_preview,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.START,
                ),
            )
            SecondaryButton(text = "Medium", onClick = { }, buttonStyle = ButtonStyle.MEDIUM)
            SecondaryButton(text = "Small", onClick = { }, buttonStyle = ButtonStyle.SMALL)
            SecondaryButton(text = "X Small", onClick = { }, buttonStyle = ButtonStyle.X_SMALL)
        }
    }
}

@Composable
private fun getButtonIconSize(buttonStyle: ButtonStyle) = dimensionResource(
    when (buttonStyle) {
        ButtonStyle.LARGE -> R.dimen.button_large_icon_size
        ButtonStyle.MEDIUM -> R.dimen.button_large_icon_size
        ButtonStyle.SMALL -> R.dimen.button_small_icon_size
        ButtonStyle.X_SMALL -> R.dimen.button_xsmall_icon_size
    },
)

@Composable
private fun getButtonShape(buttonStyle: ButtonStyle) = RoundedCornerShape(
    dimensionResource(
        when (buttonStyle) {
            ButtonStyle.LARGE -> R.dimen.radius_16
            ButtonStyle.MEDIUM -> R.dimen.radius_12
            ButtonStyle.SMALL -> R.dimen.radius_10
            ButtonStyle.X_SMALL -> R.dimen.radius_8
        },
    ),
)

@Composable
private fun getButtonContentPadding(buttonStyle: ButtonStyle) = PaddingValues(
    horizontal = dimensionResource(
        id = when (buttonStyle) {
            ButtonStyle.LARGE -> R.dimen.margin_3x
            ButtonStyle.MEDIUM -> R.dimen.margin_2_5x
            ButtonStyle.SMALL -> R.dimen.margin_2x
            ButtonStyle.X_SMALL -> R.dimen.margin_1_5x
        },
    ),
    vertical = dimensionResource(
        id = when (buttonStyle) {
            ButtonStyle.LARGE -> R.dimen.margin_2_5x
            ButtonStyle.MEDIUM -> R.dimen.margin_2x
            ButtonStyle.SMALL -> R.dimen.margin_1_5x
            ButtonStyle.X_SMALL -> R.dimen.margin_1_5x
        },
    ),
)

@Composable
private fun getIconButtonSize(buttonStyle: ButtonStyle) = dimensionResource(
    when (buttonStyle) {
        ButtonStyle.LARGE -> R.dimen.icon_button_size_large
        ButtonStyle.MEDIUM -> R.dimen.icon_button_size_medium
        ButtonStyle.SMALL -> R.dimen.icon_button_size_small
        ButtonStyle.X_SMALL -> R.dimen.icon_button_size_x_small
    },
)

@Composable
private fun getButtonTextStyle(buttonStyle: ButtonStyle) = when (buttonStyle) {
    ButtonStyle.LARGE -> typography.titleLarge
    ButtonStyle.MEDIUM -> typography.titleMedium
    ButtonStyle.SMALL -> typography.titleSmall
    ButtonStyle.X_SMALL -> typography.bodySmall
}

@Composable
private fun getButtonLoadingSize(buttonStyle: ButtonStyle) = dimensionResource(
    when (buttonStyle) {
        ButtonStyle.LARGE -> R.dimen.button_large_progress_indicator_size
        ButtonStyle.MEDIUM -> R.dimen.button_medium_progress_indicator_size
        ButtonStyle.SMALL -> R.dimen.button_small_progress_indicator_size
        ButtonStyle.X_SMALL -> R.dimen.button_xsmall_progress_indicator_size
    },
)

@Composable
fun getPrimaryButtonColors(
    overrideColor: Color?,
    overrideContentColor: Color?,
    overrideDisabledColor: Color?,
) = ButtonDefaults.buttonColors(
    containerColor = overrideColor ?: MaterialTheme.colorScheme.primary,
    contentColor = overrideContentColor ?: Color.White,
    disabledContentColor = overrideContentColor ?: Color.White,
    disabledContainerColor = overrideDisabledColor ?: Black30,
)

@Composable
fun getSecondaryButtonColors() = ButtonDefaults.outlinedButtonColors(
    contentColor = MaterialTheme.colorScheme.secondary,
    disabledContentColor = Black30,
)

@Composable
fun getSecondaryButtonBorderColors(
    buttonStyle: ButtonStyle,
    enabled: Boolean,
    enabledColor: Color = Color.Black,
) = BorderStroke(
    getBorderThickness(buttonStyle = buttonStyle),
    if (enabled) enabledColor else Black30,
)

@Composable
fun getBorderThickness(buttonStyle: ButtonStyle) = dimensionResource(
    when (buttonStyle) {
        ButtonStyle.LARGE -> R.dimen.border_width_2_5
        ButtonStyle.MEDIUM -> R.dimen.border_width_2
        ButtonStyle.SMALL -> R.dimen.border_width_1_5
        ButtonStyle.X_SMALL -> R.dimen.border_width_1_25
    },
)

data class ButtonIconInfo(
    @param:DrawableRes val iconResId: Int,
    val iconAlignment: IconAlignment = IconAlignment.START,
    val iconColor: Color? = null,
)

enum class IconAlignment {
    START,
    END,
}
