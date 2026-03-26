package com.mena97villalobos.designsystem.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens

@Composable
fun HighlightCard(
    title: String,
    mainText: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    radius: Dp = DesignSystemDimens.Radius12,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
) {
    val contentColor = contentColorFor(backgroundColor)

    Surface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(radius),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(
                horizontal = DesignSystemDimens.Margin3x,
                vertical = DesignSystemDimens.Margin2x,
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DesignSystemDimens.Margin),
            ) {
                androidx.compose.material3.Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.82f),
                    fontWeight = FontWeight.SemiBold,
                )

                androidx.compose.material3.Text(
                    text = mainText,
                    style = MaterialTheme.typography.displaySmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                )

                androidx.compose.material3.Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.82f),
                )
            }
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewHighlightCardPrimary() {
    LifeCompanionTheme {
        HighlightCard(
            title = "Days remaining",
            mainText = "42",
            subtitle = "October 15, 2024",
            modifier = Modifier.size(width = 320.dp, height = 160.dp),
        )
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewHighlightCardCustomBackground() {
    LifeCompanionTheme {
        HighlightCard(
            title = "Days remaining",
            mainText = "42",
            subtitle = "October 15, 2024",
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(width = 320.dp, height = 160.dp),
        )
    }
}

