package com.mena97villalobos.domain.security

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Behavioural tests for [PassphraseManager] on the iOS simulator
 * (`./gradlew :domain:iosSimulatorArm64Test`). Verifies that the passphrase gate applies the *same*
 * exponential backoff as the PIN (issue #9 acceptance criterion). Uses a fake repository and a
 * controllable clock for deterministic timing.
 */
class PassphraseManagerTest {

    // Same schedule as the PIN: 1–3 immediate, 4 → 30s, 5 → 5min, 6+ → 15min.
    private val config = BackoffConfig()

    @Test
    fun correctPassphraseSucceedsAndResetsAttempts() = runBlocking {
        val repo = FakePassphraseRepository().apply {
            storedPassphrase = "correct horse battery"
            failedAttempts = 2
        }
        val manager = PassphraseManager(repo, config) { 1_000L }

        val outcome = manager.submit("correct horse battery")

        assertEquals(PassphraseOutcome.Success, outcome)
        assertEquals(0, repo.failedAttempts)
    }

    @Test
    fun wrongPassphraseRecordsAttemptAndReportsRemaining() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = "correct horse battery" }
        val manager = PassphraseManager(repo, config) { 1_000L }

        val outcome = manager.submit("wrong")

        assertIs<PassphraseOutcome.Failed>(outcome)
        assertEquals(3, outcome.remainingAttemptsBeforeLockout)
        assertEquals(1, repo.failedAttempts)
    }

    @Test
    fun fourthFailureTriggersThirtySecondLockout() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = "correct horse battery" }
        var now = 1_000L
        val manager = PassphraseManager(repo, config) { now }

        repeat(3) { assertIs<PassphraseOutcome.Failed>(manager.submit("wrong")) }
        val fourth = manager.submit("wrong")

        assertIs<PassphraseOutcome.LockedOut>(fourth)
        assertEquals(30_000L, fourth.remainingMillis)

        // Even the correct passphrase is rejected while locked out.
        now = 1_000L + 10_000L
        val duringLockout = manager.submit("correct horse battery")
        assertIs<PassphraseOutcome.LockedOut>(duringLockout)
        assertEquals(20_000L, duringLockout.remainingMillis)
    }

    @Test
    fun fifthFailureEscalatesToFiveMinutes() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = "correct horse battery" }
        var now = 1_000L
        val manager = PassphraseManager(repo, config) { now }

        repeat(4) { manager.submit("wrong") }
        now = 1_000L + 30_000L + 1L
        val fifth = manager.submit("wrong")

        assertIs<PassphraseOutcome.LockedOut>(fifth)
        assertEquals(5 * 60_000L, fifth.remainingMillis)
    }

    @Test
    fun lockoutExpiresThenCorrectPassphraseSucceeds() = runBlocking {
        val repo = FakePassphraseRepository().apply { storedPassphrase = "correct horse battery" }
        var now = 1_000L
        val manager = PassphraseManager(repo, config) { now }
        repeat(4) { manager.submit("wrong") }

        now = 1_000L + 30_000L + 1L
        val outcome = manager.submit("correct horse battery")

        assertEquals(PassphraseOutcome.Success, outcome)
        assertEquals(0, repo.failedAttempts)
    }
}
