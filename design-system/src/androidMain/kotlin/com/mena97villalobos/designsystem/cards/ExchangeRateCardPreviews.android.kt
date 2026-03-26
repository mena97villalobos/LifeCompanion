package com.mena97villalobos.designsystem.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
