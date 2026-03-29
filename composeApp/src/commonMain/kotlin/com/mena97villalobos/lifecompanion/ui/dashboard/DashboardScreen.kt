package com.mena97villalobos.lifecompanion.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mena97villalobos.designsystem.cards.ExchangeRateCard
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens
import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) = Column(modifier = modifier) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is DashboardStatus.Loading -> DashboardLoading()
        is DashboardStatus.Success -> DashboardContent(currentState)
        is DashboardStatus.Error -> Text("Error loading data")
    }
}

@Composable
private fun DashboardContent(state: DashboardStatus.Success) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val cardGap = DesignSystemDimens.ExchangeRateCardGap
        val availableWidth = maxWidth - (DesignSystemDimens.Margin2x * 2)
        val responsiveCardWidth = ((availableWidth - cardGap) / 2).coerceIn(
            minimumValue = DesignSystemDimens.ExchangeRateCardMinWidth,
            maximumValue = DesignSystemDimens.ExchangeRateCardWidth,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystemDimens.Margin2x),
            horizontalArrangement = Arrangement.spacedBy(cardGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExchangeRateCard(
                currentExchangeRate = state.sellExchangeRate.rate,
                previousExchangeRate = state.sellExchangeRate.previousRate,
                indicatorLabel = state.sellExchangeRate.indicator.displayName,
                modifier = Modifier.width(responsiveCardWidth),
            )

            ExchangeRateCard(
                currentExchangeRate = state.buyExchangeRate.rate,
                previousExchangeRate = state.buyExchangeRate.previousRate,
                indicatorLabel = state.buyExchangeRate.indicator.displayName,
                modifier = Modifier.width(responsiveCardWidth),
            )
        }
    }
}

@Composable
private fun DashboardLoading() = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    CircularProgressIndicator()
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    DashboardContent(
        state = DashboardStatus.Success(
            sellExchangeRate = ExchangeRate(
                indicator = Indicator.CRC_TO_USD,
                date = "2026-01-01",
                rate = 455.05,
                previousRate = 450.0,
            ),
            buyExchangeRate = ExchangeRate(
                indicator = Indicator.USD_TO_CRC,
                date = "2026-01-01",
                rate = 450.05,
                previousRate = 445.0,
            ),
        )
    )
}
