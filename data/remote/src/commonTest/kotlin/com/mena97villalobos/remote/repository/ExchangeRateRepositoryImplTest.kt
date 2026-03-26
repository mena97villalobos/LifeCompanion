package com.mena97villalobos.remote.repository

import com.mena97villalobos.domain.model.Indicator
import kotlin.test.Test
import kotlin.test.assertEquals

class ExchangeRateRepositoryImplTest {
    @Test
    fun `USD to CRC maps to sell endpoint`() {
        assertEquals(ExchangeEndpoint.SELL, endpointFor(Indicator.USD_TO_CRC))
    }

    @Test
    fun `CRC to USD maps to buy endpoint`() {
        assertEquals(ExchangeEndpoint.BUY, endpointFor(Indicator.CRC_TO_USD))
    }
}
