package com.mena97villalobos.domain.security

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLockBackoffTest {

    private val config = BackoffConfig()

    @Test
    fun noLockoutBelowFirstTier() {
        assertEquals(0L, lockoutDurationMillis(0, config))
        assertEquals(0L, lockoutDurationMillis(1, config))
        assertEquals(0L, lockoutDurationMillis(2, config))
        assertEquals(0L, lockoutDurationMillis(3, config))
    }

    @Test
    fun fourthFailureLocksForThirtySeconds() {
        assertEquals(30_000L, lockoutDurationMillis(4, config))
    }

    @Test
    fun fifthFailureLocksForFiveMinutes() {
        assertEquals(5 * 60_000L, lockoutDurationMillis(5, config))
    }

    @Test
    fun sixthAndBeyondLockForFifteenMinutes() {
        assertEquals(15 * 60_000L, lockoutDurationMillis(6, config))
        assertEquals(15 * 60_000L, lockoutDurationMillis(7, config))
        assertEquals(15 * 60_000L, lockoutDurationMillis(100, config))
        assertEquals(15 * 60_000L, lockoutDurationMillis(Int.MAX_VALUE, config))
    }

    @Test
    fun firstLockoutAtReflectsLowestTier() {
        assertEquals(4, config.firstLockoutAt)
    }
}
