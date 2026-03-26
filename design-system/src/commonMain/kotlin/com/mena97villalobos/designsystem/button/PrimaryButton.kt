package com.mena97villalobos.designsystem.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.mena97villalobos.designsystem.core.TextColor
import com.mena97villalobos.designsystem.core.conditional
import com.mena97villalobos.designsystem.theme.Black30
import com.mena97villalobos.designsystem.theme.Blue800
import com.mena97villalobos.designsystem.theme.BrandPrimary
import com.mena97villalobos.designsystem.theme.typography
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens

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
                strokeWidth = DesignSystemDimens.ButtonProgressIndicatorStrokeWidth,
            )
        } else {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (buttonIconInfo?.iconAlignment == IconAlignment.START) {
                        Icon(
                            imageVector = buttonIconInfo.icon,
                            tint = buttonIconInfo.iconColor ?: overrideTextColor?.color
                            ?: BrandPrimary,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = DesignSystemDimens.MarginHalf)
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
                                            DesignSystemDimens.MarginHalf,
                                )
                            },
                        color = overrideTextColor?.color ?: TextColor.WHITE.color,
                        maxLines = maxLines,
                    )
                }
                if (buttonIconInfo?.iconAlignment == IconAlignment.END) {
                    Icon(
                        imageVector = buttonIconInfo.icon,
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
                        imageVector = buttonIconInfo.icon,
                        tint = if (enabled) {
                            buttonIconInfo.iconColor ?: textColor.color
                        } else {
                            Black30
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = DesignSystemDimens.MarginHalf)
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
                                        DesignSystemDimens.MarginHalf,
                            )
                        },
                    color = if (enabled) textColor.color else TextColor.BLACK40.color,
                )
            }
            if (buttonIconInfo?.iconAlignment == IconAlignment.END) {
                Icon(
                    imageVector = buttonIconInfo.icon,
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

private fun getButtonIconSize(buttonStyle: ButtonStyle): Dp =
    when (buttonStyle) {
        ButtonStyle.LARGE -> DesignSystemDimens.ButtonLargeIconSize
        ButtonStyle.MEDIUM -> DesignSystemDimens.ButtonLargeIconSize
        ButtonStyle.SMALL -> DesignSystemDimens.ButtonSmallIconSize
        ButtonStyle.X_SMALL -> DesignSystemDimens.ButtonXSmallIconSize
    }

private fun getButtonShape(buttonStyle: ButtonStyle) = RoundedCornerShape(
    when (buttonStyle) {
        ButtonStyle.LARGE -> DesignSystemDimens.Radius16
        ButtonStyle.MEDIUM -> DesignSystemDimens.Radius12
        ButtonStyle.SMALL -> DesignSystemDimens.Radius10
        ButtonStyle.X_SMALL -> DesignSystemDimens.Radius8
    },
)

private fun getButtonContentPadding(buttonStyle: ButtonStyle) = PaddingValues(
    horizontal = when (buttonStyle) {
        ButtonStyle.LARGE -> DesignSystemDimens.Margin3x
        ButtonStyle.MEDIUM -> DesignSystemDimens.Margin2_5x
        ButtonStyle.SMALL -> DesignSystemDimens.Margin2x
        ButtonStyle.X_SMALL -> DesignSystemDimens.Margin1_5x
    },
    vertical = when (buttonStyle) {
        ButtonStyle.LARGE -> DesignSystemDimens.Margin2_5x
        ButtonStyle.MEDIUM -> DesignSystemDimens.Margin2x
        ButtonStyle.SMALL -> DesignSystemDimens.Margin1_5x
        ButtonStyle.X_SMALL -> DesignSystemDimens.Margin1_5x
    },
)

private fun getButtonTextStyle(buttonStyle: ButtonStyle) = when (buttonStyle) {
    ButtonStyle.LARGE -> typography.titleLarge
    ButtonStyle.MEDIUM -> typography.titleMedium
    ButtonStyle.SMALL -> typography.titleSmall
    ButtonStyle.X_SMALL -> typography.bodySmall
}

private fun getButtonLoadingSize(buttonStyle: ButtonStyle): Dp =
    when (buttonStyle) {
        ButtonStyle.LARGE -> DesignSystemDimens.ButtonLargeProgressIndicatorSize
        ButtonStyle.MEDIUM -> DesignSystemDimens.ButtonMediumProgressIndicatorSize
        ButtonStyle.SMALL -> DesignSystemDimens.ButtonSmallProgressIndicatorSize
        ButtonStyle.X_SMALL -> DesignSystemDimens.ButtonXSmallProgressIndicatorSize
    }

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

fun getBorderThickness(buttonStyle: ButtonStyle) = when (buttonStyle) {
    ButtonStyle.LARGE -> DesignSystemDimens.BorderWidth2_5
    ButtonStyle.MEDIUM -> DesignSystemDimens.BorderWidth2
    ButtonStyle.SMALL -> DesignSystemDimens.BorderWidth1_5
    ButtonStyle.X_SMALL -> DesignSystemDimens.BorderWidth1_25
}

data class ButtonIconInfo(
    val icon: ImageVector,
    val iconAlignment: IconAlignment = IconAlignment.START,
    val iconColor: Color? = null,
)

enum class IconAlignment {
    START,
    END,
}
