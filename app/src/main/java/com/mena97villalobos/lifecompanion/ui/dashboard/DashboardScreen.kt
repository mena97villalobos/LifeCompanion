package com.mena97villalobos.lifecompanion.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mena97villalobos.designsystem.R
import com.mena97villalobos.designsystem.cards.ExchangeRateCard
import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator
import org.koin.androidx.compose.koinViewModel

@Suppress("UnusedParameter")
@Composable
fun DashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) = Column(modifier = modifier) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        is DashboardStatus.Loading -> DashboardLoading()
        is DashboardStatus.Success -> DashboardContent(state as DashboardStatus.Success)
        is DashboardStatus.Error -> Text("Error loading data")
    }
}

@Composable
private fun DashboardContent(state: DashboardStatus.Success) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.margin_2x)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExchangeRateCard(
            currentExchangeRate = state.sellExchangeRate.rate,
            previousExchangeRate = state.sellExchangeRate.previousRate,
            indicatorLabel = stringResource(state.sellExchangeRate.indicator.textResource),
        )

        ExchangeRateCard(
            currentExchangeRate = state.buyExchangeRate.rate,
            previousExchangeRate = state.buyExchangeRate.previousRate,
            indicatorLabel = stringResource(state.buyExchangeRate.indicator.textResource),
        )
    }
}

@Composable
private fun DashboardLoading() = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    CircularProgressIndicator()
    Text("Loading...")
}

@Preview
@Composable
private fun DashboardScreenPreview() = MaterialTheme {
    DashboardContent(
        state = DashboardStatus.Success(
            sellExchangeRate = ExchangeRate(
                rate = 600.0,
                previousRate = 620.0,
                indicator = Indicator.CRC_TO_USD,
                date = "09 Mar",
            ),
            buyExchangeRate = ExchangeRate(
                rate = 610.0,
                previousRate = 600.0,
                indicator = Indicator.USD_TO_CRC,
                date = "09 Mar",
            ),
        ),
    )
}
