package com.mena97villalobos.designsystem.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.formatting.ExchangeRateFormatting
import com.mena97villalobos.designsystem.theme.NegativeColor
import com.mena97villalobos.designsystem.theme.PositiveColor
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens

@Composable
fun ExchangeRateCard(
    currentExchangeRate: Double,
    previousExchangeRate: Double,
    indicatorLabel: String,
    modifier: Modifier = Modifier,
) {
    val difference = currentExchangeRate - previousExchangeRate
    val isPositive = difference >= 0

    val percentageChange =
        if (previousExchangeRate != 0.0) (difference / previousExchangeRate) * 100 else 0.0

    val targetColor = if (isPositive) PositiveColor else NegativeColor
    val trendColor by animateColorAsState(targetColor)

    val differenceText = remember(difference) {
        ExchangeRateFormatting.formatCurrencyDifference(difference)
    }
    val percentageText = remember(percentageChange) {
        ExchangeRateFormatting.formatPercentage(percentageChange, maxFractionDigits = 2)
    }
    val today = remember { ExchangeRateFormatting.formatShortDateToday() }
    val currentFormatted = remember(currentExchangeRate) {
        ExchangeRateFormatting.formatCurrencyDifference(currentExchangeRate)
    }

    Card(
        modifier = modifier
            .widthIn(max = DesignSystemDimens.ExchangeRateCardWidth)
            .height(DesignSystemDimens.ExchangeRateCardHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignSystemDimens.ExchangeRateCardElevation,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignSystemDimens.Margin2x),
        ) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd),
                shape = RoundedCornerShape(DesignSystemDimens.Radius40),
                color = trendColor.copy(alpha = 0.12f),
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = DesignSystemDimens.Margin,
                        vertical = DesignSystemDimens.MarginHalf,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (isPositive) {
                            Icons.Default.ArrowDropUp
                        } else {
                            Icons.Default.ArrowDropDown
                        },
                        contentDescription = "Trend",
                        tint = trendColor,
                    )

                    Text(
                        text = "$differenceText ($percentageText%)",
                        color = trendColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Text(
                text = currentFormatted,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$today - $indicatorLabel",
                modifier = Modifier.align(Alignment.BottomCenter),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun ExchangeRatePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ExchangeRateCard(
                currentExchangeRate = 515.42,
                previousExchangeRate = 512.75,
                indicatorLabel = "USD to CRC",
            )
            ExchangeRateCard(
                currentExchangeRate = 510.10,
                previousExchangeRate = 515.42,
                indicatorLabel = "USD to CRC",
            )
        }
    }
}
