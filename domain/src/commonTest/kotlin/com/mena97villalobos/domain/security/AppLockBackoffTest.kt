package com.mena97villalobos.domain.security

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLockBackoffTest {

    private val config = BackoffConfig(
        failureThreshold = 3,
        baseDelayMillis = 30_000L,
        maxDelayMillis = 15 * 60_000L,
    )

    @Test
    fun noLockoutBelowThreshold() {
        assertEquals(0L, lockoutDurationMillis(0, config))
        assertEquals(0L, lockoutDurationMillis(1, config))
        assertEquals(0L, lockoutDurationMillis(2, config))
    }

    @Test
    fun baseDelayAtThreshold() {
        assertEquals(30_000L, lockoutDurationMillis(3, config))
    }

    @Test
    fun doublesPerAdditionalFailure() {
        assertEquals(60_000L, lockoutDurationMillis(4, config))
        assertEquals(120_000L, lockoutDurationMillis(5, config))
        assertEquals(240_000L, lockoutDurationMillis(6, config))
    }

    @Test
    fun saturatesAtMaxDelay() {
        // 30s << many doublings would overflow without the cap; result must stay at the cap.
        assertEquals(15 * 60_000L, lockoutDurationMillis(10, config))
        assertEquals(15 * 60_000L, lockoutDurationMillis(100, config))
        assertEquals(15 * 60_000L, lockoutDurationMillis(Int.MAX_VALUE, config))
    }
}
