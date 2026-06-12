package com.mena97villalobos.local.repository

import com.mena97villalobos.domain.repository.PassphraseRepository
import com.mena97villalobos.domain.security.PinHash
import com.mena97villalobos.domain.security.PinHasher
import com.mena97villalobos.domain.security.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stores the sensitive-operations passphrase (issue #9) in [SecureStorage], hashed with [PinHasher]
 * (Argon2id). Only the Argon2id hash and salt are persisted — never the plaintext passphrase. Uses
 * its own storage keys, fully separate from the app-lock PIN.
 *
 * The same Argon2id [PinHasher] is reused for both secrets: it hashes any string, so a dedicated
 * "passphrase hasher" would be duplicated code with no benefit.
 */
class PassphraseRepositoryImpl(
    private val secureStorage: SecureStorage,
    private val hasher: PinHasher,
) : PassphraseRepository {

    private val enabledFlow = MutableStateFlow(false)

    override suspend fun isEnabled(): Boolean {
        val enabled = secureStorage.contains(KEY_HASH)
        enabledFlow.value = enabled
        return enabled
    }

    override fun observeEnabled(): Flow<Boolean> = enabledFlow.asStateFlow()

    override suspend fun setPassphrase(passphrase: String) {
        val hash = hasher.hash(passphrase)
        secureStorage.putString(KEY_HASH, hash.hash)
        secureStorage.putString(KEY_SALT, hash.salt)
        resetFailedAttempts()
        enabledFlow.value = true
    }

    override suspend fun verifyPassphrase(passphrase: String): Boolean {
        val hash = secureStorage.getString(KEY_HASH) ?: return false
        val salt = secureStorage.getString(KEY_SALT).orEmpty()
        return hasher.verify(passphrase, PinHash(hash = hash, salt = salt))
    }

    override suspend fun clearPassphrase() {
        secureStorage.remove(KEY_HASH)
        secureStorage.remove(KEY_SALT)
        resetFailedAttempts()
        enabledFlow.value = false
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

    private companion object {
        const val KEY_HASH = "passphrase_hash"
        const val KEY_SALT = "passphrase_salt"
        const val KEY_FAILED_ATTEMPTS = "passphrase_failed_attempts"
        const val KEY_LAST_FAILED_AT = "passphrase_last_failed_at"
    }
}
