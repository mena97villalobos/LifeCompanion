package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.repository.AppLockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory [AppLockRepository] for [AppLockManagerTest]; PIN comparison is plaintext for the test. */
class FakeAppLockRepository : AppLockRepository {
    var storedPin: String? = null
    var failedAttempts: Int = 0
    var lastFailedAt: Long? = null
    private val biometricEnabled = MutableStateFlow(false)
    private var inactivityTimeout = 5 * 60_000L

    override suspend fun isPinSet(): Boolean = storedPin != null

    override suspend fun setPin(pin: String) {
        storedPin = pin
        failedAttempts = 0
        lastFailedAt = null
    }

    override suspend fun verifyPin(pin: String): Boolean = storedPin == pin

    override suspend fun clearPin() {
        storedPin = null
        failedAttempts = 0
        lastFailedAt = null
    }

    override fun observeBiometricEnabled(): Flow<Boolean> = biometricEnabled.asStateFlow()

    override suspend fun isBiometricEnabled(): Boolean = biometricEnabled.value

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabled.value = enabled
    }

    override suspend fun getFailedAttempts(): Int = failedAttempts

    override suspend fun getLastFailedAtMillis(): Long? = lastFailedAt

    override suspend fun recordFailedAttempt(nowMillis: Long) {
        failedAttempts += 1
        lastFailedAt = nowMillis
    }

    override suspend fun resetFailedAttempts() {
        failedAttempts = 0
        lastFailedAt = null
    }

    override suspend fun getInactivityTimeoutMillis(): Long = inactivityTimeout

    override suspend fun setInactivityTimeoutMillis(timeoutMillis: Long) {
        inactivityTimeout = timeoutMillis
    }
}
