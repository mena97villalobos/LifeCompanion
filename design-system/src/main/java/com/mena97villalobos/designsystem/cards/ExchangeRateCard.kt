package com.mena97villalobos.designsystem.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mena97villalobos.designsystem.R
import com.mena97villalobos.designsystem.theme.NegativeColor
import com.mena97villalobos.designsystem.theme.PositiveColor
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency

private const val CRC_CURRENCY_CODE = "CRC"
private const val DATE_FORMAT_SHORT = "dd MMM"

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
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(CRC_CURRENCY_CODE)
        }
    }
    val percentFormatter = remember {
        NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 2
        }
    }
    val today = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT),
        )
    }

    Card(
        modifier = modifier
            .width(dimensionResource(R.dimen.exchange_rate_card_width))
            .height(dimensionResource(R.dimen.exchange_rate_card_height)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.exchange_rate_card_elevation),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.margin_2x)),
        ) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_40)),
                color = trendColor.copy(alpha = 0.12f),
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.margin),
                        vertical = dimensionResource(R.dimen.margin_half),
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
                        text = "${currencyFormatter.format(difference)} " +
                            "(${percentFormatter.format(percentageChange)}%)",
                        color = trendColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Text(
                text = currencyFormatter.format(currentExchangeRate),
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
            modifier = Modifier.padding(dimensionResource(R.dimen.margin_2x)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_2x)),
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
