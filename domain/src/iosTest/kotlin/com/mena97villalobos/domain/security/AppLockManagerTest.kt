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

    private val config = BackoffConfig(failureThreshold = 3, baseDelayMillis = 30_000L, maxDelayMillis = 900_000L)

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
        assertEquals(2, outcome.remainingAttemptsBeforeLockout)
        assertEquals(1, repo.failedAttempts)
    }

    @Test
    fun thirdFailureTriggersLockout() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 1_000L
        val manager = AppLockManager(repo, config) { now }
        manager.initialize()

        manager.submitPin("000000")
        manager.submitPin("000000")
        val third = manager.submitPin("000000")

        assertIs<UnlockOutcome.LockedOut>(third)
        assertEquals(30_000L, third.remainingMillis)
        assertIs<AppLockState.LockedOut>(manager.state.value)

        // Even the correct PIN is rejected while locked out.
        now = 1_000L + 10_000L
        val duringLockout = manager.submitPin("123456")
        assertIs<UnlockOutcome.LockedOut>(duringLockout)
        assertEquals(20_000L, duringLockout.remainingMillis)
    }

    @Test
    fun lockoutExpiresThenCorrectPinUnlocks() = runBlocking {
        val repo = FakeAppLockRepository().apply { storedPin = "123456" }
        var now = 1_000L
        val manager = AppLockManager(repo, config) { now }
        manager.initialize()
        repeat(3) { manager.submitPin("000000") }

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
