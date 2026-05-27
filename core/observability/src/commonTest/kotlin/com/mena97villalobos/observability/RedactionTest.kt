package com.mena97villalobos.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RedactionTest {

    @Test
    fun redactsEmailAddresses() {
        val result = redact("user mena97villalobos@gmail.com logged in")
        assertEquals("user [REDACTED_EMAIL] logged in", result)
    }

    @Test
    fun redactsColonesAmountWithCostaRicanFormatting() {
        val result = redact("payment of ₡1.234.567,89 recorded")
        assertEquals("payment of [REDACTED_AMOUNT] recorded", result)
    }

    @Test
    fun redactsUsdAmounts() {
        assertEquals("balance [REDACTED_AMOUNT]", redact("balance $1,234.56"))
        assertEquals("principal [REDACTED_AMOUNT]", redact("principal 50000 USD"))
    }

    @Test
    fun redactsMultiplePiiInOneMessage() {
        val result = redact("loan for jane.doe@bank.cr is ₡10.000.000")
        assertFalse(result.contains("jane.doe@bank.cr"))
        assertFalse(result.contains("10.000.000"))
        assertTrue(result.contains("[REDACTED_EMAIL]"))
        assertTrue(result.contains("[REDACTED_AMOUNT]"))
    }

    @Test
    fun leavesNonSensitiveTextUntouched() {
        val message = "schedule recomputed for loan 42 with 60 periods"
        assertEquals(message, redact(message))
    }
}
