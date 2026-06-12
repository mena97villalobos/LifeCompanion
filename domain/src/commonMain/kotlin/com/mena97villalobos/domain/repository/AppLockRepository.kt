package com.mena97villalobos.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Persistence for app-lock secrets and preferences, backed by the platform secure store.
 *
 * Holds the Argon2id PIN hash, the biometric-enabled flag, the failed-attempt counter used for
 * exponential backoff, and the inactivity timeout. No plaintext PIN is ever stored.
 */
interface AppLockRepository {
    /** True once a PIN has been configured (onboarding app-lock step completed). */
    suspend fun isPinSet(): Boolean

    /** Hashes [pin] with Argon2id and stores it, resetting any failed-attempt state. */
    suspend fun setPin(pin: String)

    /** Verifies [pin] against the stored hash without mutating attempt counters. */
    suspend fun verifyPin(pin: String): Boolean

    /** Removes the stored PIN hash and related lock state. */
    suspend fun clearPin()

    /** Whether the user has opted into biometric unlock. */
    fun observeBiometricEnabled(): Flow<Boolean>

    suspend fun isBiometricEnabled(): Boolean

    suspend fun setBiometricEnabled(enabled: Boolean)

    /** Consecutive failed PIN attempts, persisted so backoff survives an app restart. */
    suspend fun getFailedAttempts(): Int

    /** Epoch millis of the most recent failed attempt, or null when there is none. */
    suspend fun getLastFailedAtMillis(): Long?

    /** Records a failed attempt: increments the counter and stamps the time. */
    suspend fun recordFailedAttempt(nowMillis: Long)

    /** Clears the failed-attempt counter after a successful unlock. */
    suspend fun resetFailedAttempts()

    /** Inactivity timeout in millis after which the app auto-locks (default 5 minutes). */
    suspend fun getInactivityTimeoutMillis(): Long

    suspend fun setInactivityTimeoutMillis(timeoutMillis: Long)
}
