package com.mena97villalobos.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Persistence for the optional "sensitive operations passphrase" (issue #9), backed by the platform
 * secure store.
 *
 * This is a *separate* secret from the app-lock PIN: an extra, opt-in factor required to confirm
 * destructive actions (deleting loans, exporting/restoring data, disabling the app lock, etc.). Only
 * the Argon2id hash and salt are persisted — never the plaintext passphrase.
 *
 * The feature is considered **enabled** exactly when a passphrase hash exists: enabling sets a
 * passphrase, disabling clears it (after the current passphrase is confirmed).
 */
interface PassphraseRepository {
    /** True when a passphrase has been configured, i.e. the requirement is enabled. */
    suspend fun isEnabled(): Boolean

    /** Emits whether the requirement is currently enabled, for the settings toggle. */
    fun observeEnabled(): Flow<Boolean>

    /** Hashes [passphrase] with Argon2id and stores it, resetting any failed-attempt state. */
    suspend fun setPassphrase(passphrase: String)

    /** Verifies [passphrase] against the stored hash without mutating attempt counters. */
    suspend fun verifyPassphrase(passphrase: String): Boolean

    /** Removes the stored passphrase hash and related state (disables the requirement). */
    suspend fun clearPassphrase()

    /** Consecutive failed passphrase attempts, persisted so backoff survives an app restart. */
    suspend fun getFailedAttempts(): Int

    /** Epoch millis of the most recent failed attempt, or null when there is none. */
    suspend fun getLastFailedAtMillis(): Long?

    /** Records a failed attempt: increments the counter and stamps the time. */
    suspend fun recordFailedAttempt(nowMillis: Long)

    /** Clears the failed-attempt counter after a successful confirmation. */
    suspend fun resetFailedAttempts()
}
