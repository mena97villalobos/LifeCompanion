package com.mena97villalobos.local.repository

import com.mena97villalobos.domain.repository.AppLockRepository
import com.mena97villalobos.domain.security.PinHash
import com.mena97villalobos.domain.security.PinHasher
import com.mena97villalobos.domain.security.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores app-lock secrets in [SecureStorage] and hashes PINs with [PinHasher] (Argon2id). Only the
 * Argon2id hash and salt are persisted — never the plaintext PIN.
 */
class AppLockRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val pinHasher: PinHasher,
) : AppLockRepository {

    private val biometricEnabledFlow = MutableStateFlow(false)

    override suspend fun isPinSet(): Boolean = secureStorage.contains(KEY_PIN_HASH)

    override suspend fun setPin(pin: String) {
        val pinHash = pinHasher.hash(pin)
        secureStorage.putString(KEY_PIN_HASH, pinHash.hash)
        secureStorage.putString(KEY_PIN_SALT, pinHash.salt)
        resetFailedAttempts()
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val hash = secureStorage.getString(KEY_PIN_HASH) ?: return false
        val salt = secureStorage.getString(KEY_PIN_SALT).orEmpty()
        return pinHasher.verify(pin, PinHash(hash = hash, salt = salt))
    }

    override suspend fun clearPin() {
        secureStorage.remove(KEY_PIN_HASH)
        secureStorage.remove(KEY_PIN_SALT)
        resetFailedAttempts()
    }

    override fun observeBiometricEnabled(): Flow<Boolean> = biometricEnabledFlow.asStateFlow()

    override suspend fun isBiometricEnabled(): Boolean {
        val enabled = secureStorage.getBoolean(KEY_BIOMETRIC_ENABLED) ?: false
        biometricEnabledFlow.value = enabled
        return enabled
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        secureStorage.putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
        biometricEnabledFlow.value = enabled
    }

    override suspend fun getFailedAttempts(): Int = secureStorage.getInt(KEY_FAILED_ATTEMPTS) ?: 0

    override suspend fun getLastFailedAtMillis(): Long? = secureStorage.getLong(KEY_LAST_FAILED_AT)

    override suspend fun recordFailedAttempt(nowMillis: Long) {
        val next = (secureStorage.getInt(KEY_FAILED_ATTEMPTS) ?: 0) + 1
        secureStorage.putInt(KEY_FAILED_ATTEMPTS, next)
        secureStorage.putLong(KEY_LAST_FAILED_AT, nowMillis)
    }

    override suspend fun resetFailedAttempts() {
        secureStorage.remove(KEY_FAILED_ATTEMPTS)
        secureStorage.remove(KEY_LAST_FAILED_AT)
    }

    override suspend fun getInactivityTimeoutMillis(): Long =
        secureStorage.getLong(KEY_INACTIVITY_TIMEOUT) ?: DEFAULT_INACTIVITY_TIMEOUT_MILLIS

    override suspend fun setInactivityTimeoutMillis(timeoutMillis: Long) {
        secureStorage.putLong(KEY_INACTIVITY_TIMEOUT, timeoutMillis)
    }

    private companion object {
        const val KEY_PIN_HASH = "applock_pin_hash"
        const val KEY_PIN_SALT = "applock_pin_salt"
        const val KEY_BIOMETRIC_ENABLED = "applock_biometric_enabled"
        const val KEY_FAILED_ATTEMPTS = "applock_failed_attempts"
        const val KEY_LAST_FAILED_AT = "applock_last_failed_at"
        const val KEY_INACTIVITY_TIMEOUT = "applock_inactivity_timeout"

        const val DEFAULT_INACTIVITY_TIMEOUT_MILLIS = 5 * 60_000L
    }
}
