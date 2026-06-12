package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.model.AppLockState
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Behavioural tests for [AppLockManager] running on the iOS simulator
 * (`./gradlew :domain:iosSimulatorArm64Test`). Uses a fake in-memory [com.mena97villalobos.domain.repository.AppLockRepository]
 * and a controllable clock so backoff timing is deterministic.
 */
class AppLockManagerTest {

    // Default schedule (issue #8): 1–3 immediate, 4 → 30s, 5 → 5min, 6+ → 15min.
    private val config = BackoffConfig()

    @Test
    fun initializeUnconfiguredWhenNoPin() = runBlocking {
        val repo = FakeAppLockRepository()
        val manager = AppLockManager(repo, config) { 0L }

        manager.initialize()

        assertEquals(AppLockState.NotConfigured, manager.state.value)
    }

    @Test
    fun initializeLockedWhenPinSet() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        val manager = AppLockManager(repo, config) { 0L }

        manager.initialize()

        assertEquals(AppLockState.Locked, manager.state.value)
    }

    @Test
    fun correctPinUnlocksAndResetsAttempts() = runBlocking {
        val repo = FakeAppLockRepository().apply {
            storedPin = "123456"
            failedAttempts = 2
        }
        val manager = AppLockManager(repo, config) { 1_000L }
        manager.initialize()

        val outcome = manager.submitPin("123456")

        assertEquals(UnlockOutcome.Success, outcome)
        assertEquals(AppLockState.Unlocked, manager.state.value)
        assertEquals(0, repo.failedAttempts)
    }

    @Test
    fun wrongPinRecordsAttemptAndReportsRemaining() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        val manager = AppLockManager(repo, config) { 1_000L }
        manager.initialize()

        val outcome = manager.submitPin("000000")

        assertIs<UnlockOutcome.Failed>(outcome)
        assertEquals(3, outcome.remainingAttemptsBeforeLockout)
        assertEquals(1, repo.failedAttempts)
    }

    @Test
    fun fourthFailureTriggersLockout() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 1_000L
        val manager = AppLockManager(repo, config) { now }
        manager.initialize()

        // First three failures stay immediate-retry; the fourth engages the 30s lockout.
        repeat(3) {
            val outcome = manager.submitPin("000000")
            assertIs<UnlockOutcome.Failed>(outcome)
        }
        val fourth = manager.submitPin("000000")

        assertIs<UnlockOutcome.LockedOut>(fourth)
        assertEquals(30_000L, fourth.remainingMillis)
        assertIs<AppLockState.LockedOut>(manager.state.value)

        // Even the correct PIN is rejected while locked out.
        now = 1_000L + 10_000L
        val duringLockout = manager.submitPin("123456")
        assertIs<UnlockOutcome.LockedOut>(duringLockout)
        assertEquals(20_000L, duringLockout.remainingMillis)
    }

    @Test
    fun fifthFailureEscalatesToFiveMinutes() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 1_000L
        val manager = AppLockManager(repo, config) { now }
        manager.initialize()

        repeat(4) { manager.submitPin("000000") }
        // Wait out the 30s lockout, then fail once more to reach the 5-minute tier.
        now = 1_000L + 30_000L + 1L
        val fifth = manager.submitPin("000000")

        assertIs<UnlockOutcome.LockedOut>(fifth)
        assertEquals(5 * 60_000L, fifth.remainingMillis)
    }

    @Test
    fun lockoutExpiresThenCorrectPinUnlocks() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 1_000L
        val manager = AppLockManager(repo, config) { now }
        manager.initialize()
        repeat(4) { manager.submitPin("000000") }

        now = 1_000L + 30_000L + 1L
        val outcome = manager.submitPin("123456")

        assertEquals(UnlockOutcome.Success, outcome)
        assertEquals(AppLockState.Unlocked, manager.state.value)
        assertEquals(0, repo.failedAttempts)
    }

    @Test
    fun lockReengagesLockState() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        val manager = AppLockManager(repo, config) { 1_000L }
        manager.initialize()
        manager.submitPin("123456")
        assertEquals(AppLockState.Unlocked, manager.state.value)

        manager.lock()

        assertTrue(manager.state.value is AppLockState.Locked)
    }
}
